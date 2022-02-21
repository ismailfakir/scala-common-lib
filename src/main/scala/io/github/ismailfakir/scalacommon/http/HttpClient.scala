package io.github.ismailfakir.scalacommon.http

import java.nio.file.Path

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Accept, RawHeader}
import akka.util.ByteString
import io.github.ismailfakir.scalacommon.utils.StringUtils
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.Future
import scala.util.Try
import scala.xml.NodeSeq

case class HttpClient(request: HttpRequest)(implicit val system: ActorSystem) extends RequestBuilder {

  def headers(kvs: (String, String)*): HttpClient = {
    HttpClient(request.mapHeaders(_ ++ kvs.map((RawHeader.apply _).tupled)))
  }

  def params(kvs: (String, String)*): HttpClient = {
    val query = kvs.foldLeft(request.uri.query())((query, curr) => curr +: query)
    HttpClient(request.withUri(request.uri.withQuery(query)))
  }

  def accept(mediaRanges: Seq[MediaRange]): HttpClient =
    HttpClient(request.addHeader(Accept(mediaRanges)))

  private def acceptJson: HttpClient =
    HttpClient(request.addHeader(Accept(MediaRange(MediaTypes.`application/json`))))

  def acceptXml: HttpClient =
    HttpClient(request.addHeader(Accept(MediaRange(MediaTypes.`application/xml`))))

  def bodyAsJson(body: String): HttpClient =
    HttpClient(request.withEntity(HttpEntity(ContentTypes.`application/json`, body)))

  def bodyAsXml(body: String): HttpClient =
    HttpClient(request.withEntity(HttpEntity(ContentTypes.`text/xml(UTF-8)`, body)))

  def bodyAsText(body: String): HttpClient =
    HttpClient(request.withEntity(HttpEntity(body)))

  def bodyAsBinary(body: Array[Byte]): HttpClient =
    HttpClient(request.withEntity(HttpEntity(body)))

  def bodyAsBinary(body: ByteString): HttpClient =
    HttpClient(request.withEntity(HttpEntity(body)))

  def bodyFromFile(contentType: ContentType, file: Path, chunkSize: Int = -1): HttpClient =
    HttpClient(request.withEntity(HttpEntity.fromPath(contentType, file, chunkSize)))

  def bodyAsForm(fields: Map[String, String]): HttpClient =
    HttpClient(request.withEntity(FormData(fields).toEntity))

  def bodyAsForm(fields: (String, String)*): HttpClient =
    HttpClient(request.withEntity(FormData(fields :_*).toEntity))

  def withPath(path: String): HttpClient = {
    val uriWithPath = HttpUtils.addPath(request.uri, path)
    HttpClient(request.withUri(uriWithPath))
  }

  def withQuery(query: Seq[(String,String)]): HttpClient = {
    val uriWithQuery = HttpUtils.addQuery(request.uri,query)
    HttpClient(request.withUri(uriWithQuery))
  }

  def withJsonEntity(jsonEntity: JsValue): HttpClient = {
    val body = Json.stringify(jsonEntity)
    HttpClient(request.withEntity(HttpEntity(ContentTypes.`application/json`, body)))
  }

  def asJson(): Future[(HttpResponse, Try[JsValue])] = {
    jsonRequest(this.request)
  }

  def asXml(): Future[(HttpResponse, Try[NodeSeq])] = {
    xmlRequest(this.request)
  }

  def withBasicAuth(userName: String, password: String): HttpClient = {
    val auth = "Basic "+ StringUtils.base64(s"$userName:$password")
    this.headers(("Authorization",auth))
  }
}

object HttpClient {

  implicit val system: ActorSystem = ActorSystem("test")

  def apply(): HttpClient = {
    new HttpClient(HttpRequest())
  }

  def get(uri: String): HttpClient =
    HttpClient(HttpRequest(uri = uri))

  def head(uri: String): HttpClient =
    HttpClient(HttpRequest(method = HttpMethods.HEAD, uri = uri))

  def post(uri: String): HttpClient =
    HttpClient(HttpRequest(method = HttpMethods.POST, uri = uri))

  def put(uri: String): HttpClient =
    HttpClient(HttpRequest(method = HttpMethods.PUT, uri = uri))

  def patch(uri: String): HttpClient =
    HttpClient(HttpRequest(method = HttpMethods.PATCH, uri = uri))

  def delete(uri: String): HttpClient =
    HttpClient(HttpRequest(method = HttpMethods.DELETE, uri = uri))

}