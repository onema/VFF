/**
  * This file is part of the ONEMA VFF Package.
  * For the full copyright and license information,
  * please view the LICENSE file that was distributed
  * with this source code.
  *
  * copyright (c) 2018 - 2019,Juan Manuel Torres (http://onema.io)
  *
  * @author Juan Manuel Torres <software@onema.io>
  */
package io.onema.vff.adapter

import better.files.File
import io.onema.extensions.StreamExtensions._
import io.onema.vff.FileSystemAsync
import io.onema.vff.adapter.TestUtils._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.concurrent.duration._


class LocalAsyncTest extends FlatSpec with Matchers with MockFactory with BeforeAndAfter {

  private val fs = FileSystemAsync()
  private val directory = "/tmp/vff"
  private val path01 = s"$directory/test.txt"
  private val path02 = s"$directory/foo.txt"
  private val path03 = s"$directory/bar.txt"
  private val path04 = s"$directory/BAZ/baz.txt"
  private val path05 = s"$directory/BLAH/BLAH/blah.txt"
  private val resourcesPath = getClass.getResource("/").getPath
  private val imagePath01 = s"${resourcesPath}robot.png"
  private val imagePath02 = s"${resourcesPath}robo-copy.png"
  private val imagePath03 = s"${resourcesPath}robot-head.png"

  after {
    File("/tmp/vff/").delete(true)
  }


  "A file Update" should "properly update an existing file" in {

    // Arrange
    val content: String = "foo bar baz"
    val file = File(path01).createIfNotExists(createParents = true)

    // Act
    val future = fs.update("/tmp/vff/test.txt", content)

    // Assert
    future.result() should be (true)
    file.contentAsString should be (content)
  }

  "A file Update" should "return false if file does not exist" in {

    // Arrange
    val content: String = "foo bar baz"

    // Act
    val future = fs.update("/tmp/vff/test.txt", content)

    // Assert
    future.result() should be (false)
  }

  "A file Delete" should "remove the file from the local file system" in {

    // Arrange
    val file = File(path01).createIfNotExists(createParents = true)

    // Act
    val future = fs.delete("/tmp/vff/test.txt")

    // Assert
    future.result() should be (true)
    file.exists should be (false)
  }

  "A file Delete a non existing file" should "return false" in {

    // Arrange - Act
    val future = fs.delete("/tmp/vff/test.txt")

    // Assert
    future.result() should be (false)
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
    val future = fs.adapter.listContents("/tmp/vff")

    // Assert
    future.result().foreach(x => {
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
    val future = fs.listContents("/tmp/vff", recursive = true)

    // Assert
    future.result().foreach(x => {
      results.contains(x) should be (true)
    })
  }

  "Has" should "return true if a file exist" in {

    // Arrange
    File(path01).createIfNotExists(createParents = true)

    // Act
    val future = fs.has(path01)

    // Assert
    future.result() should be (true)
  }

  "Has" should "return false if a file does not exist" in {

    // Arrange - Act
    val future = fs.has(path01)

    // Assert
    future.result() should be (false)
  }

  "Copy" should "duplicate a file" in {

    // Arrange
    fs.write(path01, "foo bar baz")

    // Act
    val future = fs.copy(path01, path02)
    val path01Contents = fs.read(path01).result().get.mkString
    val path02Contents = fs.read(path02).result().get.mkString

    // Assert
    future.result() should be (true)
    path02Contents should be (path01Contents)
  }

  "Write" should "add content to a new file" in {

    // Arrange - Act
    val future = fs.write(path01, "foo bar baz")
    val result = future.result()
    val path01Contents = File(path01).contentAsString

    // Assert
    result should be (true)
    path01Contents should be ("foo bar baz")
  }

  "Read" should "get the contents from an existing file" in {

    // Arrange
    val future = fs.write(path01, "foo bar baz").result()

    // Act
    val path01Contents = fs.read(path01).result().get.mkString

    // Assert
    path01Contents should be ("foo bar baz")
  }

  "ReadStream" should "get and iterator for the contents of an existing file" in {

    // Arrange
    val contents =
      """"foo bar baz
         |baz bar foo"""".stripMargin
    fs.write(path01, contents).result()

    // Act
    val path01Contents = fs.read(path01).result().get.getLines

    // Assert
    path01Contents.next should be ("\"foo bar baz")
    path01Contents.next should be ("baz bar foo\"")
  }

  "Rename" should "change the name of the file" in {

    // Arrange
    File(path01).createIfNotExists(createParents = true)

    // Act
    val future = fs.rename(path01, path02)

    // Assert
    future.result() should be (true)
    fs.has(path01).result() should be (false)
    fs.has(path02).result() should be (true)

  }

  "Image" should "be copied correctly" in {

    // Arrange
    val image = fs.read(imagePath01).result().get

    // Act
    val result = fs.write(imagePath02, image.toBytes).result()
    val copyResult = fs.copy(imagePath02, imagePath03)

    // Assert
    result should be (true)
    copyResult.result() should be (true)
    fs.has(imagePath02).result() should be (true)
    fs.has(imagePath03).result() should be (true)
    fs.delete(imagePath02).wait(100.millis)
    fs.delete(imagePath03).wait(100.millis)
  }

  "Async Adapter" should "create files with in time limit" in {
    // Arrange
    val fs = FileSystemAsync()

    // Act
    val results: Seq[Boolean] = time{
      (0 to 1000).map { i =>
        fs.write(s"$directory/$i", i.toString)
      }.results(1000.millis)
    }

    // Assert
    results.foreach(_ should be (true))
  }
}
