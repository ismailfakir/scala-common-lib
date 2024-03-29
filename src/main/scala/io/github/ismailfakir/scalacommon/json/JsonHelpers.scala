package io.github.ismailfakir.scalacommon.json

import java.time.{LocalDateTime, ZonedDateTime}

import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.Reads._
import play.api.libs.json._

object JsonHelpers {

  private lazy val log = LoggerFactory.getLogger(JsonHelpers.getClass)

  implicit class JsLookupResultHelpers[T <: JsLookupResult](
      jsLookupResult: JsLookupResult
  ) {

    val optJson = jsLookupResult.toOption

    def asBool(): Option[Boolean] = {
      checkJsLookupResult[Boolean]
    }

    def asStr(): Option[String] = {
      checkJsLookupResult[String]
    }

    def asInt(): Option[Int] = {
      checkJsLookupResult[Int]
    }

    def asDouble(): Option[Double] = {
      checkJsLookupResult[Double]
    }

    def asBigDecimal(): Option[BigDecimal] = {
      checkJsLookupResult[BigDecimal]
    }

    def asZonedDate(): Option[ZonedDateTime] = {
      checkJsLookupResult[ZonedDateTime]
    }

    def asLocalDate(): Option[LocalDateTime] = {
      checkJsLookupResult[LocalDateTime]
    }

    def asObject(): Option[JsObject] = {
      checkJsLookupResult[JsObject]
    }

    def asMap(): Map[String,JsValue] = {
      asObject() match {
        case None => Map.empty[String,JsValue]
        case Some(obj) => obj.fields.toMap
      }
    }

    def asStrMap(): Map[String,String] = {
      asObject() match {
        case None => Map.empty[String,String]
        case Some(obj) =>
          obj.fields
            .map(e => (e._1,e._2.asStr()))
            .collect {
              case (key, Some(value)) => (key,value)
            }.toMap
      }
    }

    /**
      * check validity of the JsValue & log error if any
      * @return
      */
    def checkJsLookupResult[T](implicit formatT: Format[T]): Option[T] = {
      optJson match {
        case None       => Option.empty[T]
        case Some(json) => json.checkJsValue[T]
      }
    }

  }

  implicit class JsValueHelpers[T <: JsValue](json: JsValue) {

    def asBool(): Option[Boolean] = {
      checkJsValue[Boolean]
    }

    def asStr(): Option[String] = {
      checkJsValue[String]
    }

    def asInt(): Option[Int] = {
      checkJsValue[Int]
    }

    def asDouble(): Option[Double] = {
      checkJsValue[Double]
    }

    def asBigDecimal(): Option[BigDecimal] = {
      checkJsValue[BigDecimal]
    }

    def asZonedDate(): Option[ZonedDateTime] = {
      checkJsValue[ZonedDateTime]
    }

    def asLocalDate(): Option[LocalDateTime] = {
      checkJsValue[LocalDateTime]
    }

    def asObject(): Option[JsObject] = {
      checkJsValue[JsObject]
    }

    def asMap(): Map[String,JsValue] = {
      asObject() match {
        case None => Map.empty[String,JsValue]
        case Some(obj) => obj.fields.toMap[String,JsValue]
      }
    }

    def asStrMap(): Map[String,String] = {
      asObject() match {
        case None => Map.empty[String,String]
        case Some(obj) =>
          obj.fields
            .map(e => (e._1,e._2.asStr()))
            .collect {
              case (key, Some(value)) => (key,value)
            }.toMap[String,String]
      }
    }

    /**
      * check validity of the JsValue & log error if any
      * @return
      */
    def checkJsValue[T](implicit formatT: Format[T]): Option[T] = {

      val result = json.validate[T]

      result.fold(
        invalid = { fieldErrors =>
          fieldErrors.foreach { x =>
            log.info(s"field: ${x._1}, errors: ${x._2}")
          }
          Option.empty[T]
        },
        valid = Some(_)
      )

    }

  }

  implicit class JsObjectHelpers[T <: JsObject](obj: JsObject)
  {
    def addField[V](field: (String,V)): JsObject = {

      val (key,value) = field

      val jsValue: JsValue = value match {
        case bool: Boolean => JsBoolean(bool)
        case int: Int => JsNumber(int)
        case double: Double => JsNumber(double)
        case num: BigDecimal => JsNumber(num)
        case jsobj: JsObject => jsobj
        case jsarr: JsArray => jsarr
        case _ => JsNull
      }

      obj.+(key,jsValue)
    }

  }

  implicit class JsArrayHelpers[T <: JsArray](arr: JsArray)
  {
    def addEntry[V](entry: V): JsArray = {

      val jsValue: JsValue = entry match {
        case bool: Boolean => JsBoolean(bool)
        case int: Int => JsNumber(int)
        case double: Double => JsNumber(double)
        case num: BigDecimal => JsNumber(num)
        case jsobj: JsObject => jsobj
        case jsarr: JsArray => jsarr
        case _ => JsNull
      }

      arr.:+(jsValue)
    }

  }

}
