package io.github.ismailfakir.scalacommonlib.json

import net.liftweb.json.JsonAST.JValue
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

object JsonUtils {

  implicit class JsonHelpers[T<: JValue](json: JValue){

    implicit val formats = DefaultFormats

    def prettyPrint(): Unit = {
      println(prettyRender(json))
    }

    def asOptStr(): Option[String] = {
      json.extractOpt[String]
    }
  }
}
