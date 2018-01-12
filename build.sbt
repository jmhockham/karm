name := "karm"
version := "1.0"
scalaVersion := "2.12.1"

val json4sNative = "org.json4s" %% "json4s-native" % "3.5.3"
val json4sJackson = "org.json4s" %% "json4s-jackson" % "3.5.3"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  json4sNative,
  json4sJackson,
  "org.sorm-framework" % "sorm" % "0.3.21"
)