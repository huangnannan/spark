package com.hellowzk.light.spark.stages.input

import com.hellowzk.light.spark.beans.BaseConfig
import com.hellowzk.light.spark.beans.input.ClickhouseBalanceInputConfig
import com.hellowzk.light.spark.stages.BaseWorker
import com.hellowzk.light.spark.uitils.JDBCSparkUtils
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.execution.datasources.clickhouse.ClickHouseBalancedRelationProvider

import scala.collection.JavaConversions._

/**
 * <p>
 * 日期： 2019/12/24
 * <p>
 * 时间： 16:10
 * <p>
 * 星期： 星期二
 * <p>
 * 描述：
 * <p>
 * 作者： zhaokui
 *
 **/
class ClickhouseBalanceInputWorker extends BaseWorker {
  /**
   * 加载 jdbc 数据，参考 http://spark.apache.org/docs/latest/sql-data-sources-jdbc.html
   * val jdbcDF = spark.read
   * .format("jdbc")
   * .option("url", "jdbc:postgresql:dbserver")
   * .option("dbtable", "schema.tablename")
   * .option("user", "username")
   * .option("password", "password")
   * .load()
   *
   * @param bean InputItemBean
   * @param ss   SparkSession
   */
  override def process(bean: BaseConfig)(implicit ss: SparkSession): Unit = {
    val item = bean.asInstanceOf[ClickhouseBalanceInputConfig]
    val filterd = JDBCSparkUtils.filterValues(item)
    item.dbtable.foreach { case (src, dist) =>
      filterd.put("dbtable", src)
      val reader = ss.sqlContext.read.format(classOf[ClickHouseBalancedRelationProvider].getName).options(filterd)
      val df = reader.load()
      df.createOrReplaceTempView(dist)
      logger.info(s"inputs, clickhouse BalancedClickhouseDataSource load table '$src' to Spark table '$dist' success.")
    }
    afterProcess(item)
  }


}
