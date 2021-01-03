package io.github.ismailfakir.scalacommon.http.example1

import java.time.Clock

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import io.github.ismailfakir.scalacommon.http.example1.ExampleModel.{DomainErrorObject, GatewayException, MySuccessObject}
import io.github.ismailfakir.scalacommon.http.example1.core.{DomainError, HttpClient, HttpClientConfig, HttpClientFailure, HttpClientResponse, HttpClientSuccess, HttpMetrics, RetryConfig}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

object ExampleModel {
  case class MySuccessObject(foo: String)
  case class DomainErrorObject(errorMessage: String)
  case class GatewayException(msg: String) extends RuntimeException(msg)
}

object SimpleExample extends App {

  // https://github.com/moia-dev/scala-http-client


  implicit val system: ActorSystem                                = ActorSystem("test")
  implicit val executionContext: ExecutionContext                 = system.dispatcher
  implicit val um1: Unmarshaller[HttpResponse, MySuccessObject]   = ???
  implicit val um2: Unmarshaller[HttpResponse, DomainErrorObject] = ???

  // create the client
  val httpClient = new HttpClient(
    config           = HttpClientConfig("http", "127.0.0.1", 8888),
    name             = "TestClient",
    httpMetrics      = HttpMetrics.none,
    retryConfig      = RetryConfig.default,
    clock            = Clock.systemUTC()
  )

  // make a request
  val response: Future[HttpClientResponse] = httpClient.request(
    method   = HttpMethods.POST,
    entity   = HttpEntity.apply("Example"),
    path     = "/test",
    headers  = Seq.empty,
    deadline = Deadline.now + 10.seconds
  )

  // map the response to your model
  response.flatMap {
    case HttpClientSuccess(content) => Unmarshal(content).to[MySuccessObject].map(Right(_))
    case DomainError(content)       => Unmarshal(content).to[DomainErrorObject].map(Left(_))
    case failure: HttpClientFailure => throw GatewayException(failure.toString)
  }
}
