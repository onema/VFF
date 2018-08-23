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
package io.onema.vff.adapter

import java.util

import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import io.onema.vff.FileSystem
import io.onema.vff.adapter.AwsS3AdapterTest.TestResult
import io.onema.vff.extensions.StreamExtensions._
import io.onema.vff.extensions.StringExtensions._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}


class AwsS3AdapterTest extends FlatSpec with Matchers with MockFactory with BeforeAndAfter {

//  val fs = new FileSystem(AwsS3Adapter("ones-test-bucket"))
  val path01 = "/tmp/vff/test.txt"
  val path02 = "/tmp/vff/foo.txt"
  val path03 = "/tmp/vff/bar.txt"
  val path04 = "/tmp/vff/BAZ/baz.txt"
  val path05 = "/tmp/vff/BLAH/BLAH/blah.txt"
  val bucketName = "fooBucket"

//  after {
//    fs.delete(path01)
//    fs.delete(path02)
//    fs.delete(path03)
//    fs.delete(path04)
//    fs.delete(path05)
//  }

  private def addPutObjectRequest(client: AmazonS3, validationFunc: PutObjectRequest => Boolean, response: PutObjectResult, times: Int = 1) = {
    (client.putObject(_: PutObjectRequest))
      .expects(where {validationFunc})
      .returning(response)
      .repeat(times)
  }

  private def addDoesObjectExist(client: AmazonS3, result: Boolean, times: Int = 1): Unit = {
    (client.doesObjectExist(_: String, _: String)).expects(*, *).returning(value = result).repeat(times)
  }

  private def addGetObjectRequest(client: AmazonS3, path: String, response: S3Object, times: Int = 1): Unit = {
    (client.getObject(_: String, _: String)).expects(bucketName, path).returning(response).repeat(times)
  }

  "A file Update" should "properly update an existing file" in {

    // Arrange
    val content: String = "foo bar baz"
    val request = new PutObjectRequest(bucketName, path01, content.toInputStream, new ObjectMetadata())
    val  clientMock = mock[AmazonS3]
    addPutObjectRequest(
      client = clientMock,
      validationFunc = { x => x.getBucketName == bucketName && x.getKey == path01.ltrim && x.getInputStream.mkString == ""},
      response = new PutObjectResult
    )
    addPutObjectRequest(
      client = clientMock,
      validationFunc = { x => x.getBucketName == bucketName && x.getKey == path01.ltrim && x.getInputStream.mkString == content },
      response = new PutObjectResult
    )
    addDoesObjectExist(clientMock, result = true)
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))
    fs.write(path01, "")

    // Act
    val result = fs.update(path01, content)

    // Assert
    result should be (true)
//    val result2 = fs.read(path01).get.mkString
//    result2 should be (content)
  }

  "A file Update" should "return false if file does not exist" in {

    // Arrange
    val content: String = "foo bar baz"
    val  clientMock = mock[AmazonS3]
    addDoesObjectExist(clientMock, result = false)
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))

    // Act
    val result = fs.update("/tmp/vff/test.txt", content)

    // Assert
    result should be (false)
  }

  "A file Delete" should "remove the file from the local file system" in {

    // Arrange
    val  clientMock = mock[AmazonS3]
    (clientMock.putObject(_: PutObjectRequest)).expects(*).once()
    (clientMock.deleteObject(_: String, _: String))
      .expects(bucketName, path01.ltrim)
      .once()
    addDoesObjectExist(clientMock, result = true)
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))
    fs.write(path01, "")

    // Act
    val result = fs.delete("/tmp/vff/test.txt")

    // Assert
    result should be (true)
  }

  "A file Delete a non existing file" should "return false" in {

    // Arrange - Act
    val  clientMock = mock[AmazonS3]
    (clientMock.deleteObject(_: String, _: String)).expects(*, *).never()
    addDoesObjectExist(clientMock, result = false)
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))
    val result = fs.delete("/tmp/vff/test.txt")

    // Assert
    result should be (false)
  }

  "ListContents in a directory" should "return a list of all the files" in {

    // Arrange
    val results = Seq("/tmp/vff/BLAH", "/tmp/vff/foo.txt", "/tmp/vff/bar.txt", "/tmp/vff/BAZ", "/tmp/vff/test.txt")
    val listObjectResult = new TestResult(results)
    val  clientMock = mock[AmazonS3]
    (clientMock.listObjectsV2(_: String, _: String)).expects(bucketName, "/tmp/vff").returns(listObjectResult).once()
    addPutObjectRequest(
      client = clientMock,
      validationFunc = { x => x.getBucketName == bucketName },
      response = new PutObjectResult,
      times = 5
    )
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))
    fs.write(results(0), "")
    fs.write(results(1), "")
    fs.write(results(2), "")
    fs.write(results(3), "")
    fs.write(results(4), "")

    // Act
    val response = fs.adapter.listContents("/tmp/vff")

    // Assert
    response.foreach(x => {
      results.contains(x) should be (true)
    })
  }

  "ListContents recursively in a directory" should "return a list of all the files" in {

    // Arrange
    val results = Seq("/tmp/vff/BLAH", "/tmp/vff/BLAH/BLAH", "/tmp/vff/BLAH/BLAH/blah.txt", "/tmp/vff/foo.txt", "/tmp/vff/bar.txt", "/tmp/vff/BAZ", "/tmp/vff/BAZ/baz.txt", "/tmp/vff/test.txt")
    val listObjectResult = new TestResult(results)
    val  clientMock = mock[AmazonS3]
    (clientMock.listObjectsV2(_: String, _: String)).expects(bucketName, "/tmp/vff").returns(listObjectResult).once()
    addPutObjectRequest(
      client = clientMock,
      validationFunc = { x => x.getBucketName == bucketName },
      response = new PutObjectResult,
      times = 5
    )
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))
    fs.write(path01, "")
    fs.write(path02, "")
    fs.write(path03, "")
    fs.write(path04, "")
    fs.write(path05, "")

    // Act
    val result = fs.listContents("/tmp/vff", recursive = true)

    // Assert
    result.foreach(x => {
      results.contains(x) should be (true)
    })
  }

  "Has" should "return true if a file exist" in {

    // Arrange
    val  clientMock = mock[AmazonS3]
    addDoesObjectExist(clientMock, result = true)
    addPutObjectRequest(
      client = clientMock,
      validationFunc = { x => x.getInputStream.mkString == ""},
      response = new PutObjectResult
    )
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))
    fs.write(path01, "")

    // Act
    val result = fs.has(path01)

    // Assert
    result should be (true)
  }

  "Has" should "return false if a file does not exist" in {

    // Arrange - Act
    val  clientMock = mock[AmazonS3]
    addDoesObjectExist(clientMock, result = false)
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))
    val result = fs.has(path01)

    // Assert
    result should be (false)
  }

  "Copy" should "duplicate a file" in {

    // Arrange
    val content = "foo bar baz"
    val  clientMock = mock[AmazonS3]
    addPutObjectRequest(
      client = clientMock,
      validationFunc = { x => x.getBucketName == bucketName && x.getKey == path01.ltrim && x.getInputStream.mkString == content},
      response = new PutObjectResult
    )
    (clientMock.copyObject(_: String, _: String, _: String, _: String)).expects(bucketName, path01.ltrim, bucketName, path02.ltrim).repeat(1)
    addDoesObjectExist(clientMock, result = true, times = 2)
    val response1 = new S3Object()
    response1.setObjectContent(content.toInputStream)
    val response2 = new S3Object()
    response2.setObjectContent(content.toInputStream)
    addGetObjectRequest(clientMock, path01.ltrim,  response1)
    addGetObjectRequest(clientMock, path02.ltrim,  response2)
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))

    // Act
    fs.write(path01, content)
    val result = fs.copy(path01, path02)
    val path01Contents = fs.read(path01).get.mkString
    val path02Contents = fs.read(path02).get.mkString

    // Assert
    result should be (true)
    path02Contents should be (path01Contents)
  }

  "Write" should "add content to a new file" in {

    // Arrange
    val content = "foo bar baz"
    val  clientMock = mock[AmazonS3]
    addPutObjectRequest(
      client = clientMock,
      validationFunc = { x => x.getBucketName == bucketName && x.getKey == path01.ltrim && x.getInputStream.mkString == content },
      response = new PutObjectResult
    )
    val response = new S3Object()
    response.setObjectContent(content.toInputStream)
    addGetObjectRequest(clientMock, path01.ltrim, response)
    addDoesObjectExist(clientMock, result = true)
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))

    // Act
    val result = fs.write(path01, content)
    val path01Contents = fs.read(path01).get.mkString

    // Assert
    result should be (true)
    path01Contents should be ("foo bar baz")
  }

  "Read" should "get the contents from an existing file" in {

    // Arrange
    val content = "foo bar baz"
    val  clientMock = mock[AmazonS3]
    addPutObjectRequest(
      client = clientMock,
      validationFunc = { x => x.getInputStream.mkString == content },
      response = new PutObjectResult
    )
    val response = new S3Object()
    response.setObjectContent(content.toInputStream)
    addGetObjectRequest(clientMock, path01.ltrim, response)
    addDoesObjectExist(clientMock, result = true)
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))

    // Act
    val result = fs.write(path01, content)
    val path01Contents = fs.readAsString(path01)

    // Assert
    path01Contents.getOrElse("This is not the correct String") should be ("foo bar baz")
  }

  "Read Iterator" should "get and iterator for the contents of an existing file" in {

    // Arrange
    val content = "foo bar baz\nbaz bar foo"
    val  clientMock = mock[AmazonS3]
    addPutObjectRequest(
      client = clientMock,
      validationFunc = { x => x.getInputStream.mkString == content },
      response = new PutObjectResult
    )
    val response = new S3Object()
    response.setObjectContent(content.toInputStream)
    addGetObjectRequest(clientMock, path01.ltrim, response)
    addDoesObjectExist(clientMock, result = true)
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))
    fs.write(path01, content)

    // Act
    val path01Contents = fs.read(path01).get.getLines

    // Assert
    path01Contents.next should be ("foo bar baz")
    path01Contents.next should be ("baz bar foo")
  }

  "Rename" should "change the name of the file" in {

    // Arrange
    val  clientMock = mock[AmazonS3]
    addPutObjectRequest(
      client = clientMock,
      validationFunc = { x => x.getInputStream.mkString == "" },
      response = new PutObjectResult
    )
    (clientMock.copyObject(_: String, _: String, _: String, _: String)).expects(bucketName, path01.ltrim, bucketName, path02.ltrim).repeat(1)
    (clientMock.deleteObject(_: String, _: String))
      .expects(bucketName, path01.ltrim)
      .once()
    addDoesObjectExist(clientMock, result = false)
    addDoesObjectExist(clientMock, result = true)
    val fs = new FileSystem(new AwsS3Adapter(clientMock, bucketName))
    fs.write(path01, "")

    // Act
    val result = fs.rename(path01, path02)

    // Assert
    result should be (true)
    fs.has(path01) should be (false)
    fs.has(path02) should be (true)

  }

}

object AwsS3AdapterTest {
  class TestResult(val results: Seq[String]) extends ListObjectsV2Result {
    override def getObjectSummaries: util.List[S3ObjectSummary] = {
      val one = new S3ObjectSummary()
      one.setKey(results(0))
      val two = new S3ObjectSummary()
      two.setKey(results(1))
      val three = new S3ObjectSummary()
      three.setKey(results(2))
      val four = new S3ObjectSummary()
      four.setKey(results(3))
      val five = new S3ObjectSummary()
      five.setKey(results(4))
      util.Arrays.asList(one, two, three, four, five)
    }
  }

}
