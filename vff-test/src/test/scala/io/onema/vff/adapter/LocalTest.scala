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

import java.util.UUID

import better.files.File
import io.onema.extensions.StreamExtensions._
import io.onema.vff.adapter.TestUtils._
import io.onema.vff.{FileSystem, FileSystemAsync}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._


class LocalTest extends FlatSpec with Matchers with MockFactory with BeforeAndAfter {

  private val fs = new FileSystem(new Local)
  private val path01 = "/tmp/vff/test.txt"
  private val path02 = "/tmp/vff/foo.txt"
  private val path03 = "/tmp/vff/bar.txt"
  private val path04 = "/tmp/vff/BAZ/baz.txt"
  private val path05 = "/tmp/vff/BLAH/BLAH/blah.txt"
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
    val result = fs.update("/tmp/vff/test.txt", content)

    // Assert
    result should be (true)
    file.contentAsString should be (content)
  }

  "A file Update" should "return false if file does not exist" in {

    // Arrange
    val content: String = "foo bar baz"

    // Act
    val result = fs.update("/tmp/vff/test.txt", content)

    // Assert
    result should be (false)
  }

  "A file Delete" should "remove the file from the local file system" in {

    // Arrange
    val file = File(path01).createIfNotExists(createParents = true)

    // Act
    val result = fs.delete("/tmp/vff/test.txt")

    // Assert
    result should be (true)
    file.exists should be (false)
  }

  "A file Delete a non existing file" should "return false" in {

    // Arrange - Act
    val result = fs.delete("/tmp/vff/test.txt")

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
    val result = fs.adapter.listContents("/tmp/vff")

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
    val result = fs.listContents("/tmp/vff", recursive = true)

    // Assert
    result.foreach(x => {
      results.contains(x) should be (true)
    })
  }

  "Has" should "return true if a file exist" in {

    // Arrange
    File(path01).createIfNotExists(createParents = true)

    // Act
    val result = fs.has(path01)

    // Assert
    result should be (true)
  }

  "Has" should "return false if a file does not exist" in {

    // Arrange - Act
    val result = fs.has(path01)

    // Assert
    result should be (false)
  }

  "Copy" should "duplicate a file" in {

    // Arrange
    fs.write(path01, "foo bar baz")

    // Act
    val result = fs.copy(path01, path02)
    val path01Contents = fs.read(path01).get.mkString
    val path02Contents = fs.read(path02).get.mkString

    // Assert
    result should be (true)
    path02Contents should be (path01Contents)
  }

  "Write" should "add content to a new file" in {

    // Arrange - Act
    val result = fs.write(path01, "foo bar baz")
    val path01Contents = File(path01).contentAsString

    // Assert
    result should be (true)
    path01Contents should be ("foo bar baz")
  }

  "Read" should "get the contents from an existing file" in {

    // Arrange
    val result = fs.write(path01, "foo bar baz")

    // Act
    val path01Contents = fs.read(path01).get.mkString

    // Assert
    path01Contents should be ("foo bar baz")
  }

  "ReadStream" should "get and iterator for the contents of an existing file" in {

    // Arrange
    val contents =
      """"foo bar baz
         |baz bar foo"""".stripMargin
    val result = fs.write(path01, contents)

    // Act
    val path01Contents = fs.read(path01).get.getLines

    // Assert
    path01Contents.next should be ("\"foo bar baz")
    path01Contents.next should be ("baz bar foo\"")
  }

  "Rename" should "change the name of the file" in {

    // Arrange
    File(path01).createIfNotExists(createParents = true)

    // Act
    val result = fs.rename(path01, path02)

    // Assert
    result should be (true)
    fs.has(path01) should be (false)
    fs.has(path02) should be (true)

  }

  "Image" should "be copied correctly" in {

    // Arrange
    val image = fs.read(imagePath01).get

    // Act
    val result = fs.write(imagePath02, image.toBytes)
    val copyResult = fs.copy(imagePath02, imagePath03)

    // Assert
    result should be (true)
    copyResult should be (true)
    fs.has(imagePath02) should be (true)
    fs.has(imagePath03) should be (true)
    fs.delete(imagePath02)
    fs.delete(imagePath03)
  }

  "Async Adapter" should "create files with in time limit" in {
    // Arrange
    val fs = FileSystemAsync()
    val uuid = UUID.randomUUID().toString
    val dir = s"/tmp/$uuid"
    println(dir)
    Await.result(fs.write(s"$dir/tmp", "0"), 500.millis)

    // Act
    val results: Seq[Boolean] = time{
      (0 to 1000).map { i =>
        fs.write(s"$dir/$i", i.toString)
      }.map(Await.result(_, 1000.millis))
    }

    // Assert
    results.foreach(_ should be (true))
  }

//  "sync Adapter" should "do things synchronously" in {
//    // Arrange
//    val fs = FileSystem()
//    val uuid = UUID.randomUUID().toString
//    val dir = s"/tmp/$uuid"
//    println(dir)
//    fs.write(s"$dir/tmp", "0")
//
//    // Act
//    val results: Seq[Boolean] = time{
//      (0 to 1000).map { i =>
//        fs.write(s"$dir/$i", i.toString)
//      }
//    }
//
//    // Assert
//    results.foreach(_ should be (true))
//  }
}
