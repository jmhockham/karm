name := "karm"
version := "1.0"
scalaVersion := "2.11.12"

val json4sNative = "org.json4s" %% "json4s-native" % "3.5.3"
val json4sJackson = "org.json4s" %% "json4s-jackson" % "3.5.3"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  json4sNative,
  json4sJackson,
  "org.sorm-framework" % "sorm" % "0.3.21",
  "com.h2database" % "h2" % "1.4.192",
  "org.apache.kafka" %% "kafka" % "0.10.1.1",
  "org.apache.commons" % "commons-lang3" % "3.3.2",
  "commons-logging" % "commons-logging" % "1.2",
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1",
  "org.postgresql" % "postgresql" % "42.1.1"
)

dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value

//ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }