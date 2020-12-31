package io.github.ismailfakir.scalacommon.collection

object Map {

  def merge[A, B](a: Map[A, B], b: Map[A, B])(mergef: (B, Option[B]) => B): Map[A, B] = {
    val (big, small) = if (a.size > b.size) (a, b) else (b, a)
    small.foldLeft(big) { case (z, (k, v)) => z + (k -> mergef(v, z.get(k))) }
  }

  def mergeIntSum[A](a: Map[A, Int], b: Map[A, Int]): Map[A, Int] =
    merge(a, b)((v1, v2) => v2.map(_ + v1).getOrElse(v1))

  def mergeStringSum[A](a: Map[A, String], b: Map[A, String]): Map[A, String] =
    merge(a, b)((v1, v2) => v2.map(_ + v1).getOrElse(v1))

}
