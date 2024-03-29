package com.hellowzk.light.spark.beans.output

import com.hellowzk.light.spark.stages.output.ClickhouseBalanceOutputWorker

/**
 * <p>
 * 日期： 2020/5/19
 * <p>
 * 时间： 15:13
 * <p>
 * 星期： 星期二
 * <p>
 * 描述：
 * <p>
 * 作者： zhaokui
 *
 **/
class ClickhouseBalanceOutputConfig extends JDBCOutputConfig {
  driver = "ru.yandex.clickhouse.ClickHouseDriver"

  override def checkNoneIsBlank(): Unit = {
    validateNoneIsBlank("url", "user", "password", "tables")
  }

  setWorkerClass(classOf[ClickhouseBalanceOutputWorker].getName)
}
