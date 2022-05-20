package com.hellowzk.light.spark.beans.input

import com.hellowzk.light.spark.stages.input.ClickhouseBalanceInputWorker

import scala.collection.JavaConversions._

/**
 * <p>
 * 日期： 2020/5/19
 * <p>
 * 时间： 15:19
 * <p>
 * 星期： 星期二
 * <p>
 * 描述：详细参数 http://spark.apache.org/docs/latest/sql-data-sources-jdbc.html
 * <p>
 * 作者： zhaokui
 *
 **/
class ClickhouseBalanceInputConfig extends JDBCInputConfig {

  driver = "ru.yandex.clickhouse.ClickHouseDriver"

  override def checkNoneIsBlank(): Unit = {
    validateNoneIsBlank("url", "user", "password", "dbtable")
  }

  setWorkerClass(classOf[ClickhouseBalanceInputWorker].getName)

  override def getDefinedTables(): List[String] = {
    dbtable.values().toList
  }
}
