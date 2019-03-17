/**
  * This file is part of the ONEMA extensions Package.
  * For the full copyright and license information,
  * please view the LICENSE file that was distributed
  * with this source code.
  *
  * copyright (c) 2019, Juan Manuel Torres (http://onema.io)
  *
  * @author Juan Manuel Torres <software@onema.io>
  */

package io.onema.vff.adapter

import com.typesafe.scalalogging.Logger
import scala.concurrent.duration._

import scala.concurrent.{Await, Future}

object TestUtils {
  val log = Logger("testing")
  def time[T](codeBlock: => T): T = {
    val startTime = System.nanoTime()
    val result = codeBlock
    val endTime = System.nanoTime()
    println(s"Time: ${((endTime - startTime)/1000000).toFloat} ms")
    result
  }

  implicit class ResultExtensions[T](future: Future[T]) {
    def result(duration: FiniteDuration = 1000.millis): T = {
      Await.result(future, duration)
    }
    def wait(duration: FiniteDuration): Unit = Await.ready(future, duration)
  }

  implicit class ResutltsExtenstions[T](sequence: Seq[Future[T]]){
    def awaitAll(duration: FiniteDuration = 1000.millis): Unit = {
      sequence.foreach(Await.ready(_, duration))
    }

    def results(duration: FiniteDuration = 1000.millis): Seq[T] = {
      sequence.map(Await.result(_, duration))
    }
  }
}
