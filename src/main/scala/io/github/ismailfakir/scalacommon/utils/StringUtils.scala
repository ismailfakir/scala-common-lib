package io.github.ismailfakir.scalacommon.utils

import java.nio.charset.Charset
import java.util.Base64

import akka.http.scaladsl.model.HttpCharsets

object StringUtils {

  def base64(input: String): String = {
    Base64.getEncoder.encodeToString(input.getBytes(HttpCharsets.`UTF-8`.nioCharset()))
  }

}
