package io.github.ismailfakir.scalacommon.utils

import java.util.UUID

object UniqueId {

  def newId(): String = {
    UUID.randomUUID().toString
  }

}
