/**
  * This file is part of the ONEMA vff Package.
  * For the full copyright and license information,
  * please view the LICENSE file that was distributed
  * with this source code.
  *
  * copyright (c) 2018, Juan Manuel Torres (http://onema.io)
  *
  * @author Juan Manuel Torres <software@onema.io>
  */

package io.onema.vff.extensions

import java.io.{ByteArrayInputStream, InputStream, SequenceInputStream}

import better.files._

import scala.collection.JavaConverters._
import scala.io.{BufferedSource, Source}

object StreamExtensions {

  implicit class BufferedSourceExtensions(bufferedSource: BufferedSource) {

    def toBytes: Iterator[Byte] = {
      bufferedSource.mkString.getBytes.toIterator
    }
  }

  implicit class InputStreamExtensions(inputStream: InputStream) {

    def getLines: Iterator[String] = {
      Source.fromInputStream(inputStream).getLines()
    }

    def mkString: String = {
      Source.fromInputStream(inputStream).mkString
    }

    def toBytes: Iterator[Byte] = inputStream.bytes

    def toByteArray : Array[Byte] = inputStream.bytes.toArray
  }
}
