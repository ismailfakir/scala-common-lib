package io.github.ismailfakir.scalacommon.json

import net.liftweb.json.{DefaultFormats, JValue, compactRender, prettyRender}
import play.api.libs.json.{JsValue, Json}

object LiftJsonUtils {

  implicit class LiftJsonHelpers[T <: JValue](json: JValue) {

    implicit val formats = DefaultFormats

    def prettyPrint(): Unit = {
      println(prettyRender(json))
    }

    def asOptStr(): Option[String] = {
      json.extractOpt[String]
    }

    /**
      * convert lift json to play json
      * @return
      */
    def toJsValue(): JsValue = {
      val str = compactRender(json)
      Json.parse(str)
    }

  }
}
