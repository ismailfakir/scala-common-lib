package io.github.ismailfakir.scalacommon.http

import akka.http.scaladsl.model.Uri

object HttpUtils {
  // https://github.com/public-apis/public-apis

  def addPath(uri: Uri, path: String): Uri =
  {
    uri.withPath(Uri.Path(path))
  }

  def addQuery(uri: Uri, query: Seq[(String,String)]): Uri =
  {
    uri.withQuery(Uri.Query(query: _*))
  }

}
