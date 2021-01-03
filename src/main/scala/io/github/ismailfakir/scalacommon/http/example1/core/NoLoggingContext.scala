package io.github.ismailfakir.scalacommon.http.example1.core

trait NoLoggingContext
object NoLoggingContext extends NoLoggingContext {
  implicit val noLoggingContext: NoLoggingContext = NoLoggingContext
}