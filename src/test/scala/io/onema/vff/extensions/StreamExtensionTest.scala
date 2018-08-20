/**
  * This file is part of the ONEMA VFF Package.
  * For the full copyright and license information,
  * please view the LICENSE file that was distributed
  * with this source code.
  *
  * copyright (c) 2018, Juan Manuel Torres (http://onema.io)
  *
  * @author Juan Manuel Torres <software@onema.io>
  */

package io.onema.vff.extensions

import java.io.ByteArrayInputStream

import io.onema.vff.extensions.StreamExtensions._
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source


class StreamExtensionTest extends FlatSpec with Matchers {
  private val resourcesPath = getClass.getResource("/").getPath
  private val path01 = s"${resourcesPath}test.txt"
  private val input =
    """foo
      |bar baz
      |blah""".stripMargin

  "A BufferedSource " should "be converted to bytes and have the proper content length" in {
    // Arrange
    val bufferedSource = Source.fromFile(path01)

    // Act
    val bytes = bufferedSource.toBytes

    // Assert
    bytes.length should be(16)
  }

  "A BufferedSource " should "be converted to bytes and have the proper content" in {
    // Arrange
    val bufferedSource = Source.fromFile(path01)

    // Act
    val bytes = bufferedSource.toBytes
    val str = new String(bytes.toArray)

    // Assert
    str should be(input)
  }

  "An InputStream" should "Create an iterator" in {
    // Arrange
    val inputStream = new ByteArrayInputStream(input.getBytes())

    // Act
    val iterator = inputStream.getLines

    // Assert
    iterator.next should be("foo")
    iterator.next should be("bar baz")
    iterator.next should be("blah")
  }

  "An InputStream" should "make a string" in {
    // Arrange
    val inputStream = new ByteArrayInputStream(input.getBytes())

    // Act
    val str = inputStream.mkString

    // Assert
    str should be(input)
  }

  "An InputStream " should "be converted to bytes and have the proper content" in {
    // Arrange
    val inputStream = new ByteArrayInputStream(input.getBytes())

    // Act
    val bytes = inputStream.toBytes
    val str = new String(bytes.toArray)

    // Assert
    str should be(input)
  }

  "An InputStream " should "be converted to bytes and have the proper content length" in {
    // Arrange
    val inputStream = new ByteArrayInputStream(input.getBytes())

    // Act
    val bytes = inputStream.toBytes
    val length = bytes.length

    // Assert
    length should be(16)
  }

  "An InputStream " should "be converted to byte array and have the proper content" in {
    // Arrange
    val inputStream = new ByteArrayInputStream(input.getBytes())

    // Act
    val bytes = inputStream.toByteArray
    val str = new String(bytes)

    // Assert
    str should be(input)
  }

  "An InputStream " should "be converted to byte array and have the proper content length" in {
    // Arrange
    val inputStream = new ByteArrayInputStream(input.getBytes())

    // Act
    val bytes = inputStream.toByteArray
    val length = bytes.length

    // Assert
    length should be(16)
  }
}
