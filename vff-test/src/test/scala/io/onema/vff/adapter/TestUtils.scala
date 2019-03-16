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

object TestUtils {
  val log = Logger("testing")
  def time[T](codeBlock: => T): T = {
    val startTime = System.nanoTime()
    val result = codeBlock
    val endTime = System.nanoTime()
    println(s"Time: ${((endTime - startTime)/1000000).toFloat} ms")
    result
  }
}
