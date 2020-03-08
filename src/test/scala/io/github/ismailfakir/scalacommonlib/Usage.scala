package io.github.ismail.fakir.scalacommonlib

import io.github.ismailfakir.scalacommonlib.ScalaCommonLib
import scalaprops.{Property, Scalaprops}

object Usage extends Scalaprops {

  val testDoNothing =
// #do-nothing
    Property.forAll { x: Int =>
      ScalaCommonLib.doNothing(x) == x
    }
// #do-nothing

}
