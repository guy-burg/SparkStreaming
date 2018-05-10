name := "SparkStreaming"

version := "0.1"

scalaVersion := "2.11.0"

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-aws" % "2.7.3",
  "org.apache.spark" %% "spark-core" % "2.3.0",
  "org.apache.spark" %% "spark-sql" % "2.3.0",
  "org.apache.spark" %% "spark-streaming" % "2.3.0",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.3.0",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "org.json4s" %% "json4s-jackson" % "3.5.2"
)