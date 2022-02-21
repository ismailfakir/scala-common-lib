package io.github.ismailfakir.scalacommon.http

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.{JsValue, Json}

import scala.util.{Failure, Success}

class RequestBuilderTests extends AnyFlatSpec with RequestBuilder {

  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher

  "Htpclient" should "should get response" in {
    val request = HttpRequest(uri = "http://www.google.com")
    val fut = rawRequest(request)

    fut.andThen{
      case Success(value) => println(value._2.getOrElse(""))
      case Failure(exception) => println(exception)
    }

  }

}
