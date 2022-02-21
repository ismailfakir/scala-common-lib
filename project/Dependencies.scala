import sbt._
import Keys._

object Dependencies {

  lazy val AkkaVersion = "2.6.8"
  lazy val AkkaHttpVersion = "10.2.2"
  lazy val akka = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
  )

  lazy val json = Seq(
    "com.typesafe.play" %% "play-json" % "2.9.1"
  )

  lazy val xml = Seq(
    "org.scala-lang.modules" %% "scala-xml" % "2.0.0-M3"
  )


  lazy val logging = Seq(
    "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2",
    "ch.qos.logback"              % "logback-classic" % "1.2.3" % Test
  )

  lazy val test = Seq(
    "org.scalactic" %% "scalactic" % "3.1.1",
    "org.scalatest" %% "scalatest" % "3.1.1" % "test"
  )

  val all = akka ++ json ++ xml ++ logging ++ test

}
