import sbt._
import Keys._

object Dependencies {

  val AkkaVersion = "2.6.8"
  val AkkaHttpVersion = "10.2.2"
  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
  )

  val json = Seq(
    "com.typesafe.play" %% "play-json" % "2.9.1"
  )

  val test = Seq(
    "org.scalactic" %% "scalactic" % "3.1.1",
    "org.scalatest" %% "scalatest" % "3.1.1" % "test"
  )

  val all = akka ++ json ++ test

}
