package com.cloudlock.sparkstreaming

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.apache.spark.sql._
import org.apache.spark.sql.streaming.Trigger
import org.json4s._
import org.json4s.jackson.JsonMethods._

object Main extends LazyLogging {

  case class Kdata(orgPk: String, value: String)

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("JsonToParquet")
      .getOrCreate()

    import spark.implicits._

    spark.sparkContext.hadoopConfiguration.set("fs.s3.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.awsAccessKeyId", "")
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.awsSecretAccessKey", "")


    val messages = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "192.168.1.24:9092,192.168.1.11:9092,192.168.1.16:9092,192.168.2.29:9092,192.168.2.47:9092")
      .option("subscribe", "uba_enriched")
      .option("startingOffsets", "latest")
      .option("consumer.cache.maxCapacity", "160")
      .load()

    val query = messages
      .select('value cast "string")
      .map(i => i.getString(0))
      .map(i => {
        val orgPK = jsonStrToMap(i)("org_pk").toString
        val value = i
        Kdata(orgPK, value)
      })
      .repartition($"orgPK")
      .writeStream
      .trigger(Trigger.ProcessingTime("60 seconds"))
      .format("csv")
      .option("format", "append")
      .option("checkpointLocation", "/tmp/spark_streaming/checkpoint_path/")
      .option("path", "/tmp/spark_streaming/output/")
      //      .option("path", "s3a://guy-dev-bucket/")
      .start()

    query.awaitTermination()

  }

  def jsonStrToMap(jsonStr: String): Map[String, Any] = {
    implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats
    parse(jsonStr).extract[Map[String, Any]]
  }
}
