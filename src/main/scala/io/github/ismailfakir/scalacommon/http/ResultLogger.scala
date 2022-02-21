package io.github.ismailfakir.scalacommon.http

import akka.http.scaladsl.model.{HttpMethod, HttpMethods, HttpRequest, HttpResponse}
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

trait ResultLogger extends LazyLogging {

  def logRequest(requestId: String, request: HttpRequest)(implicit ec: ExecutionContext, mat: Materializer): Unit = {
    val body = request.method match {
      case HttpMethods.POST | HttpMethods.PUT =>
        val futBody= request.entity.toStrict(1 second).map(_.data).map(_.utf8String)
        Await.result(futBody, 1 second)
      case _ => ""
    }
    logJson(requestToJson(requestId,request,body))
  }

  def logResponse(requestId: String, responseF: Future[HttpResponse])(implicit ec: ExecutionContext, mat: Materializer): Future[Unit] = {
    val responseAndEntity = responseF.flatMap { response =>
      response.entity.toStrict(3 seconds).map(entity => (response, entity))
    }

    responseAndEntity
      .map {
        case (response, entity) =>
          logSuccess(requestId,response, entity.data.utf8String)
          ()
      }
      .recover {
        case ex =>
          logFailure(requestId,ex)
          ()
      }
  }

  private def logSuccess(requestId: String,response: HttpResponse, entity: String) = {
    logJson(responseToJson(requestId,response,entity))
  }

  private def logFailure(requestId: String,failure: Throwable): Unit = {
    logJson(failureToJson(requestId,failure))
  }

  def logJson(json: JsValue): Unit ={
    logger.info("\n"+Json.prettyPrint(json)+"\n")
  }

  def failureToJson(requestId: String,failure: Throwable): JsValue = {
    Json.obj(
      "RequestId"-> requestId,
      "Status"-> "failed",
      "cause"-> failure.getMessage
    )
  }

  def responseToJson(requestId: String,response: HttpResponse, entity: String): JsValue = {
    Json.obj(
      "RequestId"-> requestId,
      "Response Status"->response.status.intValue(),
      "Response Headers"-> response.headers.map(h => h.name()->h.value()).toMap[String,String],
      "Response"-> entity
    )
  }

  def requestToJson(requestId: String,request: HttpRequest, entity: String): JsValue = {
    Json.obj(
      "RequestId"-> requestId,
      "Request Method"->request.method.value,
      "Request Url"->request.uri.toString(),
      "Request Headers"-> request.headers.map(h => h.name()->h.value()).toMap[String,String],
      "Request Body"-> entity
    )
  }

}
