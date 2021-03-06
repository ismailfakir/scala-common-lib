package io.github.ismailfakir.scalacommon.json

import io.github.ismailfakir.scalacommon.json.JsonHelpers.JsValueHelpers
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.{JsNumber, JsValue, Json}

class JsonHelpersTests extends AnyFlatSpec {

  val json: JsValue = Json.parse("""
    {
      "name" : "Watership Down",
      "location" : {
        "lat" : 51.235685,
        "long" : -1.309197
      },
      "residents" : [ {
        "name" : "Fiver",
        "age" : 4,
        "role" : null
      }, {
        "name" : "Bigwig",
        "age" : 6,
        "role" : "Owsla"
      } ]
    }
    """)

  "A JsValue" should "converted to Map" in {

    val jvMap = (json \ "location").get.asMap()
    assert(jvMap.isInstanceOf[Map[String,JsValue]])

    val jn = JsNumber(BigDecimal(51.235685))
    val mv:Option[JsValue] = jvMap.get("lat")
    assert( mv== Some(jn))
  }

}
