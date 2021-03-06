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


import java.io.InputStream

import better.files._
import com.typesafe.scalalogging.Logger

import scala.util.{Failure, Success, Try}

object Local {
  def apply: Local = new Local()
}

class Local extends Adapter {

  //--- Fields ---
  protected val log = Logger("vff")

  //--- Methods ---

  /**
    * Check if the file exists
    */
  override def has(path: String): Boolean = File(path).exists

  /**
    * Read a file
    */
  def read(path: String): Option[InputStream] = {
    if(has(path)) {
      Option(File(path).newFileInputStream)
    } else {
      None
    }
  }

  /**
    * List contents of a directory
    */
  override def listContents(directory: String, recursive: Boolean): Seq[String] = {
    val dir = if(recursive) File(directory).listRecursively else File(directory).list
    dir.map(x => x.path.toString).toSeq
  }

  /**
    * Get file's size in KB
    */
  override def size(path: String): Long = File(path).size

  /**
    * Write a new file
    */
  def write(path: String, contents: Iterator[Byte]): Boolean = {
    val file = File(path)
    file.createIfNotExists(createParents = true)
    Try(file.writeBytes(contents)) match {
      case Success(_) => true
      case Failure(ex) =>
        log.debug(s"Unable to write to path $path. Exception: $ex")
        false
    }
  }

  /**
    * Update an existing file
    */
  override def update(path: String, contents: Iterator[Byte]): Boolean = {
    if(has(path)) write(path, contents)
    else {
      log.debug(s"Update was not successful. File $path does not exist")
      false
    }
  }

  /**
    * Rename a file
    */
  override def rename(path: String, newPath: String): Boolean = {
    Try(File(path).renameTo(newPath)) match {
      case Success(_) => true
      case Failure(ex) =>
        log.debug(s"Unable to rename path $path to $newPath. Exception: $ex")
        false
    }
  }

  /**
    * Copy a file
    */
  override def copy(path: String, newPath: String): Boolean = {
    val existingFile = File(path)
    Try(existingFile.copyTo(File(newPath))) match {
      case Success(_) => true
      case Failure(ex) =>
        log.debug(s"Unable to copy path $path to $newPath. Exception: $ex")
        false
    }
  }

  /**
    * Delete a file
    */
  override def delete(path: String): Boolean = {
    Try(File(path).delete()) match {
      case Success(_) => true
      case Failure(ex) =>
        log.debug(s"Unable to delete path $path. Exception: $ex")
        false
    }
  }
}
