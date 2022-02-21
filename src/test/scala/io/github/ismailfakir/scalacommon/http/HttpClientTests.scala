package io.github.ismailfakir.scalacommon.http

import akka.actor.ActorSystem
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import io.github.ismailfakir.scalacommon.xml.XmlHelpers._
import scala.util.{Failure, Random, Success}

class HttpClientTests extends AnyFlatSpec with LazyLogging {
  implicit val system: ActorSystem = ActorSystem("test")
  //implicit val executionContext: ExecutionContext = system.dispatcher
  import system.dispatcher // to get an implicit ExecutionContext into scope

  /*"Htpclient" should "should get response" in {

    val query = Seq("animal_type" -> "cat", "amount" -> "2")

    val fut = HttpClient
      .get("https://cat-fact.herokuapp.com")
      .withPath("/facts/random")
      .withQuery(query)
      .asJson()

    fut.andThen {
      case Success(value) =>
        value._2 match {
          case Success(json) =>
            value._1.discardEntityBytes()
            //logger.info(Json.prettyPrint(json))
          case Failure(exception) => println(exception)
        }
      case Failure(exception) => println(exception)
    }

  }*/

  /*"Htpclient" should "should post data" in {
    val i = Random.alphanumeric.take(6).mkString("")
    val j = Random.alphanumeric.take(12).mkString("")
    val json = Json.obj(s"name" -> s"cat-$i", "movies" -> Seq(s"Matrix-$j"))

    val fut = HttpClient
      .post("https://reqres.in/api/users")
      .bodyAsJson(json.toString())
      .asJson()

    fut.andThen {
      case Success(value) =>
        value._2 match {
          case Success(json) =>
            value._1.discardEntityBytes()
            //logger.info(Json.prettyPrint(json))
          case Failure(exception) => println(exception)
        }
      case Failure(exception) => println(exception)
    }
  }*/

  "Htpclient" should "should get xml data" in {

    val fut = HttpClient
      .get("https://v2.jokeapi.dev/joke/Any?format=xml")
      .asXml()

    fut.andThen {
      case Success(value) =>
        value._2 match {
          case Success(xml) =>
            value._1.discardEntityBytes()
          logger.info(prettyFy(xml))
          case Failure(exception) => println(exception)
        }
      case Failure(exception) => println(exception)
    }
  }

}
