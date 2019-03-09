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

import java.io.InputStream

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AwsS3Async {
  def apply(bucketName: String): AwsS3Async = {
    val s3 = AmazonS3ClientBuilder.defaultClient()
    new AwsS3Async(s3, bucketName)
  }

  def apply(bucketName: String, region: Regions): AwsS3Async = {
    val s3 = AmazonS3ClientBuilder.standard()
      .withRegion(region)
      .build()
    new AwsS3Async(s3, bucketName)
  }
}

class AwsS3Async(val s3Client: AmazonS3, bucketName: String) extends AdapterAsync {

  //--- Fields ---
  protected val log = Logger("vff")
  private val s3 = new AwsS3Adapter(s3Client, bucketName)

  //--- Methods ---

  /**
    * Check if the file exists
    */
  override def has(path: String): Future[Boolean] = Future {
    s3.has(path)
  }

  /**
    * Read a file
    */
  def read(path: String): Future[Option[InputStream]] = Future {
    s3.read(path)
  }

  /**
    * List contents of a directory
    */
  override def listContents(directory: String, recursive: Boolean): Future[Seq[String]] = Future {
    s3.listContents(directory, recursive)
  }

  /**
    * Get file's size in KB
    */
  override def size(path: String): Future[Long] = Future {
    s3.size(path)
  }

  /**
    * Write a new file
    */
  def write(path: String, contents: Iterator[Byte]): Future[Boolean] = Future {
    s3.write(path, contents)
  }

  /**
    * Update an existing file
    */
  override def update(path: String, contents: Iterator[Byte]): Future[Boolean] = Future {
    s3.update(path, contents)
  }

  /**
    * Rename a file
    */
  override def rename(path: String, newPath: String): Future[Boolean] = Future {
    s3.rename(path, newPath)
  }

  /**
    * Copy a file
    */
  override def copy(path: String, newPath: String): Future[Boolean] = Future {
    s3.copy(path, newPath)
  }

  /**
    * Delete a file
    */
  override def delete(path: String): Future[Boolean] = Future {
    s3.delete(path)
  }

  /**
    * Read a file
    */
  override def readAsString(path: String): Future[Option[String]] = Future {
    s3.readAsString(path)
  }

  /**
    * Read a file
    */
  override def readAsIterator(path: String): Future[Option[Iterator[String]]] = Future {
    s3.readAsIterator(path)
  }
}
