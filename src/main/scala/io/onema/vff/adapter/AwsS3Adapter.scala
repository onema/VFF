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

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.typesafe.scalalogging.Logger
import io.onema.vff.extensions.StringExtensions._

import scala.collection.JavaConverters._
import scala.io.Source.fromInputStream
import scala.util.{Failure, Success, Try}




object AwsS3Adapter {
  def apply(bucketName: String): AwsS3Adapter = {
    val s3 = AmazonS3ClientBuilder.defaultClient()
    new AwsS3Adapter(s3, bucketName)
  }

  def apply(bucketName: String, region: Regions): AwsS3Adapter = {
    val s3 = AmazonS3ClientBuilder.standard()
      .withRegion(region)
      .build()
    new AwsS3Adapter(s3, bucketName)
  }
}

class AwsS3Adapter(val s3: AmazonS3, bucketName: String) extends Adapter {

  //--- Fields ---
  protected val log = Logger("vff")

  //--- Methods ---
  /**
    * Check if the file exists
    */
  override def has(path: String): Boolean = {
    Try(s3.doesObjectExist(bucketName, path.ltrim)) match {
      case Success(result) => result
      case Failure(ex) =>
        log.debug(s"Unable to check if path $path exists. Exception $ex")
        false
    }
  }

  /**
    * Read a file
    */
  override def readStream(path: String): Iterator[String] = {
    if(has(path.ltrim)) {
      Try(s3.getObject(bucketName, path.ltrim).getObjectContent) match {
        case Success(result) =>
          return fromInputStream(result).getLines()
        case Failure(ex) =>
          log.debug(s"Unable to read file $path. Exception $ex")
      }
    }
    List[String]().toIterator
  }

  override def read(path: String): Option[String] = {
    val fileStream = readStream(path)
    if(fileStream.nonEmpty) Some(fileStream.mkString)
    else None
  }

  /**
    * List contents of a directory
    */
  override def listContents(directory: String, recursive: Boolean): Seq[String] = {
    val objects = s3.listObjectsV2(bucketName, directory).getObjectSummaries.asScala
    objects.map(_.getKey)
  }

  /**
    * Get file's size in KB
    */
  override def size(path: String): Long = {
    val s3Object = s3.getObjectMetadata(bucketName, path.ltrim)
    s3Object.getContentLength / 1000L
  }

  /**
    * Write a new file
    */
  override def write(path: String, contents: String): Boolean = {
    Try(s3.putObject(bucketName, path.ltrim, contents)) match {
      case Success(response) =>
        log.debug(s"File successfully uploaded to $bucketName with key $path")
        true
      case Failure(ex) =>
        log.debug(s"Unable to write path $path. Exception: $ex")
        false
    }
  }

  /**
    * Write a new file from a buffered source
    */
  def write(path: String, contents: Iterator[String]): Boolean = {
    // TODO: Use the S3 API to write the buffered source to s3 directly
    write(path, contents.mkString)
  }


  /**
    * Update an existing file
    */
  override def update(path: String, contents: String): Boolean = {
    if(has(path.ltrim)) {
      write(path.ltrim, contents)
    } else {
      log.debug(s"Unable to update file. The fiel $path does not exist in $bucketName")
      false
    }
  }

  /**
    * Rename a file
    */
  override def rename(path: String, newPath: String): Boolean = {
    try {
      s3.copyObject(bucketName, path.ltrim, bucketName, newPath.ltrim)
      s3.deleteObject(bucketName, path.ltrim)
      true
    } catch {
      case ex: Exception =>
        log.debug(s"Unable to rename file $path to $newPath. Exception $ex")
        false
    }
  }

  /**
    * Copy a file
    */
  override def copy(path: String, newPath: String): Boolean = {
    Try(s3.copyObject(bucketName, path.ltrim, bucketName, newPath.ltrim)) match {
      case Success(_) => true
      case Failure(ex) =>
        log.debug(s"Unable to copy file $path to $newPath. Exception $ex")
        false
    }
  }

  /**
    * Delete a file
    */
  override def delete(path: String): Boolean = {
    if(has(path)) {
      Try(s3.deleteObject(bucketName, path.ltrim)) match {
        case Success(_) => true
        case Failure(ex) =>
          log.debug(s"Unable to delete file $path. Exception $ex")
          false
      }
    } else false
  }
}