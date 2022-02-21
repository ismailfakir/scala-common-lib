package io.github.ismailfakir.scalacommon.xml

import scala.xml.{Elem, Node, NodeSeq}

object XmlHelpers {

  def prettyFy(xml:NodeSeq): String = {
    val p = new scala.xml.PrettyPrinter(80, 4)
    p.formatNodes(xml)
  }

}
