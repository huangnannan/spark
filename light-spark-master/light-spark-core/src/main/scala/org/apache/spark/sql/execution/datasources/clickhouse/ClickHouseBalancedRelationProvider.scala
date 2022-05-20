package org.apache.spark.sql.execution.datasources.clickhouse

import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils._
import org.apache.spark.sql.execution.datasources.jdbc.{JDBCOptions, JdbcRelationProvider}
import org.apache.spark.sql.sources.BaseRelation
import org.apache.spark.sql.{AnalysisException, DataFrame, SQLContext, SaveMode}

/**
 * <p>
 * 日期： 2021/5/21
 * <p>
 * 时间： 15:48
 * <p>
 * 星期： 星期五
 * <p>
 * 描述：
 * <p>
 * 作者： zhaokui
 * <p>
 * 适配加载 clickhouse BalancedClickhouseDataSource 的方法
 **/
class ClickHouseBalancedRelationProvider extends JdbcRelationProvider {
  override def shortName(): String = "clickhouse-cluster"

  override def createRelation(
                               sqlContext: SQLContext,
                               mode: SaveMode,
                               parameters: Map[String, String],
                               df: DataFrame): BaseRelation = {
    val options = new JDBCOptions(parameters)
    val isCaseSensitive = sqlContext.conf.caseSensitiveAnalysis

    val conn = ClickHouseUtils.createConnectionFactory(options)()
    try {
      val tableExists = ClickHouseUtils.tableExists(conn, options)
      if (tableExists) {
        mode match {
          case SaveMode.Overwrite =>
            if (options.isTruncate && isCascadingTruncateTable(options.url) == Some(false)) {
              // In this case, we should truncate table and then load.
              truncateTable(conn, options.table)
              val tableSchema = ClickHouseUtils.getSchemaOption(conn, options)
              saveTable(df, tableSchema, isCaseSensitive, options)
            } else {
              // Otherwise, do not truncate the table, instead drop and recreate it
              dropTable(conn, options.table)
              createTable(conn, df, options)
              saveTable(df, Some(df.schema), isCaseSensitive, options)
            }

          case SaveMode.Append =>
            val tableSchema = ClickHouseUtils.getSchemaOption(conn, options)
            saveTable(df, tableSchema, isCaseSensitive, options)

          case SaveMode.ErrorIfExists =>
            throw new AnalysisException(
              s"Table or view '${options.table}' already exists. SaveMode: ErrorIfExists.")

          case SaveMode.Ignore =>
          // With `SaveMode.Ignore` mode, if table already exists, the save operation is expected
          // to not save the contents of the DataFrame and to not change the existing data.
          // Therefore, it is okay to do nothing here and then just return the relation below.
        }
      } else {
        createTable(conn, df, options)
        saveTable(df, Some(df.schema), isCaseSensitive, options)
      }
    } finally {
      conn.close()
    }

    createRelation(sqlContext, parameters)
  }

}
