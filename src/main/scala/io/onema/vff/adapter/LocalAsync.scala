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

import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class LocalAsync extends AdapterAsync {

  //--- Fields ---
  protected val log = Logger("vff")
  private val local = new Local()

  //--- Methods ---

  /**
    * Check if the file exists
    */
  override def has(path: String): Future[Boolean] = Future {
    local.has(path)
  }

  /**
    * Read a file
    */
  def read(path: String): Future[Option[InputStream]] = Future {
    local.read(path)
  }

  /**
    * List contents of a directory
    */
  override def listContents(directory: String, recursive: Boolean): Future[Seq[String]] = Future {
    local.listContents(directory, recursive)
  }

  /**
    * Get file's size in KB
    */
  override def size(path: String): Future[Long] = Future {
    local.size(path)
  }

  /**
    * Write a new file
    */
  def write(path: String, contents: Iterator[Byte]): Future[Boolean] = Future {
    local.write(path, contents)
  }

  /**
    * Update an existing file
    */
  override def update(path: String, contents: Iterator[Byte]): Future[Boolean] = Future {
    local.update(path, contents)
  }

  /**
    * Rename a file
    */
  override def rename(path: String, newPath: String): Future[Boolean] = Future {
    local.rename(path, newPath)
  }

  /**
    * Copy a file
    */
  override def copy(path: String, newPath: String): Future[Boolean] = Future {
    local.copy(path, newPath)
  }

  /**
    * Delete a file
    */
  override def delete(path: String): Future[Boolean] = Future {
    local.delete(path)
  }

  /**
    * Read a file
    */
  override def readAsString(path: String): Future[Option[String]] = Future {
    local.readAsString(path)
  }

  /**
    * Read a file
    */
  override def readAsIterator(path: String): Future[Option[Iterator[String]]] = Future {
    local.readAsIterator(path)
  }
}
