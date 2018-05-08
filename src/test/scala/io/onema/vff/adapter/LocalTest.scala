/**
  * This file is part of the ONEMA VFF Package.
  * For the full copyright and license information,
  * please view the LICENSE file that was distributed
  * with this source code.
  *
  * copyright (c) 2018, Juan Manuel Torres (http://onema.io)
  *
  * @author Juan Manuel Torres <kinojman@gmail.com>
  */
package io.onema.vff.adapter

import better.files.File
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}


class LocalTest extends FlatSpec with Matchers with MockFactory with BeforeAndAfter {

  val adapter = new Local
  val path01 = "/tmp/vff/test.txt"
  val path02 = "/tmp/vff/foo.txt"
  val path03 = "/tmp/vff/bar.txt"
  val path04 = "/tmp/vff/BAZ/baz.txt"
  val path05 = "/tmp/vff/BLAH/BLAH/blah.txt"

  after {
    File("/tmp/vff/").delete(true)
  }


  "A file Update" should "properly update an existing file" in {

    // Arrange
    val content: String = "foo bar baz"
    val file = File(path01).createIfNotExists(createParents = true)

    // Act
    val result = adapter.update("/tmp/vff/test.txt", content)

    // Assert
    result should be (true)
    file.contentAsString should be (content)
  }

  "A file Update" should "return false if file does not exist" in {

    // Arrange
    val content: String = "foo bar baz"

    // Act
    val result = adapter.update("/tmp/vff/test.txt", content)

    // Assert
    result should be (false)
  }

  "A file Delete" should "remove the file from the local file system" in {

    // Arrange
    val file = File(path01).createIfNotExists(createParents = true)

    // Act
    val result = adapter.delete("/tmp/vff/test.txt")

    // Assert
    result should be (true)
    file.exists should be (false)
  }

  "A file Delete a non existing file" should "return false" in {

    // Arrange - Act
    val result = adapter.delete("/tmp/vff/test.txt")

    // Assert
    result should be (false)
  }

  "ListContents in a directory" should "return a list of all the files" in {

    // Arrange
    val results = Seq("/tmp/vff/BLAH", "/tmp/vff/foo.txt", "/tmp/vff/bar.txt", "/tmp/vff/BAZ", "/tmp/vff/test.txt")
    File(path01).createIfNotExists(createParents = true)
    File(path02).createIfNotExists(createParents = true)
    File(path03).createIfNotExists(createParents = true)
    File(path04).createIfNotExists(createParents = true)
    File(path05).createIfNotExists(createParents = true)

    // Act
    val result = adapter.listContents("/tmp/vff")

    // Assert
    result.foreach(x => {
      results.contains(x) should be (true)
    })
  }

  "ListContents recursively in a directory" should "return a list of all the files" in {

    // Arrange
    val results = Seq("/tmp/vff/BLAH", "/tmp/vff/BLAH/BLAH", "/tmp/vff/BLAH/BLAH/blah.txt", "/tmp/vff/foo.txt", "/tmp/vff/bar.txt", "/tmp/vff/BAZ", "/tmp/vff/BAZ/baz.txt", "/tmp/vff/test.txt")
    File(path01).createIfNotExists(createParents = true)
    File(path02).createIfNotExists(createParents = true)
    File(path03).createIfNotExists(createParents = true)
    File(path04).createIfNotExists(createParents = true)
    File(path05).createIfNotExists(createParents = true)

    // Act
    val result = adapter.listContents("/tmp/vff", recursive = true)

    // Assert
    result.foreach(x => {
      results.contains(x) should be (true)
    })
  }

  "Has" should "return true if a file exist" in {

    // Arrange
    File(path01).createIfNotExists(createParents = true)

    // Act
    val result = adapter.has(path01)

    // Assert
    result should be (true)
  }

  "Has" should "return false if a file does not exist" in {

    // Arrange - Act
    val result = adapter.has(path01)

    // Assert
    result should be (false)
  }

  "Copy" should "duplicate a file" in {

    // Arrange
    adapter.write(path01, "foo bar baz")

    // Act
    val result = adapter.copy(path01, path02)
    val path01Contents = adapter.read(path01)
    val path02Contents = adapter.read(path02)

    // Assert
    result should be (true)
    path02Contents should be (path01Contents)
  }

  "Write" should "add content to a new file" in {

    // Arrange - Act
    val result = adapter.write(path01, "foo bar baz")
    val path01Contents = File(path01).contentAsString

    // Assert
    result should be (true)
    path01Contents should be ("foo bar baz")
  }

  "Read" should "get the contents from an existing file" in {

    // Arrange
    val result = adapter.write(path01, "foo bar baz")

    // Act
    val path01Contents = adapter.read(path01)

    // Assert
    path01Contents.getOrElse("This is not the correct String") should be ("foo bar baz")
  }

  "Rename" should "change the name of the file" in {

    // Arrange
    File(path01).createIfNotExists(createParents = true)

    // Act
    val result = adapter.rename(path01, path02)

    // Assert
    result should be (true)
    adapter.has(path01) should be (false)
    adapter.has(path02) should be (true)

  }
}
