package io.github.ismailfakir.scalacommon.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{Flow, Sink, Source, SourceQueueWithComplete}
import akka.stream.{OverflowStrategy, QueueOfferResult}
import com.typesafe.scalalogging.LazyLogging
import io.github.ismailfakir.scalacommon.utils.UniqueId
import play.api.libs.json.{JsNull, JsValue, Json}

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import scala.xml.{Node, NodeSeq}
import scala.xml.XML.loadString


trait RequestBuilder extends LazyLogging with ResultLogger {

  protected lazy val logRequest = true
  protected lazy val logResponse = true

  protected lazy val bufferSize = 10
  protected lazy val maxNumberOfRequests = 50

  protected lazy val requestTimeout: FiniteDuration = 30 seconds
  protected lazy val defaultCharset = HttpCharsets.`UTF-8`

  implicit val system: ActorSystem
  import system.dispatcher // to get an implicit ExecutionContext into scope

  def poolClientFlow(uri: Uri): Flow[
    (HttpRequest, Promise[HttpResponse]),
    (Try[HttpResponse], Promise[HttpResponse]),
    Http.HostConnectionPool
  ] = {
    val host = uri.authority.host.toString()
    val port = uri.effectivePort
    uri.scheme.toLowerCase match {
      case "https" =>
        Http().cachedHostConnectionPoolHttps[Promise[HttpResponse]](host = host, port = port)
      case "http" =>
        Http().newHostConnectionPool[Promise[HttpResponse]](host = host, port = port)
      case s: String =>
        throw new IllegalArgumentException(s"Not a valid scheme '$s'")
    }

  }

  def createRequestQueue(
      uri: Uri
  ): SourceQueueWithComplete[(HttpRequest, Promise[HttpResponse])] = {
    val clientFlow = poolClientFlow(uri)
    Source
      .queue[(HttpRequest, Promise[HttpResponse])](bufferSize, OverflowStrategy.backpressure)
      .throttle(elements = maxNumberOfRequests, per = 1 minute)
      .groupedWithin(50, 1 second)
      .mapConcat(identity)
      .via(clientFlow)
      .to(Sink.foreach({
        case ((Success(resp), p)) => p.success(resp)
        case ((Failure(e), p)) => p.failure(e)
      }))
      .run()
  }

  def queueRequest(request: HttpRequest): Future[HttpResponse] = {
    val responsePromise = Promise[HttpResponse]()
    val uri = request.uri
    val queue = createRequestQueue(uri)
    queue.offer(request -> responsePromise).flatMap {
      case QueueOfferResult.Enqueued => responsePromise.future
      case QueueOfferResult.Dropped =>
        Future.failed(new RuntimeException("Queue overflowed. Try again later."))
      case QueueOfferResult.Failure(ex) => Future.failed(ex)
      case QueueOfferResult.QueueClosed =>
        Future.failed(
          new RuntimeException(
            "Queue was closed (pool shut down) while running the request. Try again later."
          )
        )
    }
  }

  def baseRequest(req: HttpRequest): Future[HttpResponse] = {
    val id = UniqueId.newId()
    if (logRequest) logRequest(id, req)
    val res = queueRequest(req)
    if (logResponse) logResponse(id, res)
    res
  }

  def rawRequest(
      req: HttpRequest,
      extraHeaders: Seq[HttpHeader] = Nil
  ): Future[(HttpResponse, Option[String])] = {

    val requestWithHeaders = req.withHeaders(req.headers ++ extraHeaders)

    for {
      resp <- baseRequest(requestWithHeaders)
      strictResp <- resp.toStrict(requestTimeout)
    } yield strictResp.entity match {
      case en: HttpEntity.Strict if en.data.length > 1 =>
        val charsetName = defaultCharset.nioCharset().name()
        val entityData = en.data.decodeString(charsetName)
        resp -> Some(entityData)
      case _ => resp -> None
    }
  }

  def jsonRequest(req: HttpRequest): Future[(HttpResponse, Try[JsValue])] = {
    def mediaJson: MediaType = MediaTypes.`application/json`
    val jsonHeaders = List(
      headers.Accept(List(MediaRange(mediaJson)))
    )
    for (resp <- rawRequest(req, jsonHeaders))
    yield resp match {
      case (hres, Some(str)) => hres -> Try(Json.parse(str))
      case _ => resp._1 -> Try(JsNull)
    }
  }

  def xmlRequest(req: HttpRequest): Future[(HttpResponse, Try[NodeSeq])] = {
    def mediaXml: MediaType = MediaTypes.`application/xml`
    val xmlHeaders = List(
      headers.Accept(List(MediaRange(mediaXml)))
    )
    for (resp <- rawRequest(req, xmlHeaders))
      yield resp match {
        case (hres, Some(str)) => hres -> Try(loadString(str))
        case _ => resp._1 -> Try(NodeSeq.Empty)
      }
  }

}
