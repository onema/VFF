/**
  * This file is part of the ONEMA Default (Template) Project Package.
  * For the full copyright and license information,
  * please view the LICENSE file that was distributed
  * with this source code.
  *
  * copyright (c) 2018, Juan Manuel Torres (http://onema.io)
  *
  * @author Juan Manuel Torres <software@onema.io>
  */

package io.onema.vff.adapter

import java.io._

import io.onema.vff.extensions.StreamExtensions._

import scala.concurrent.Future

trait AdapterAsync {

  /**
    * Check if the file exists
    */
  def has(path: String): Future[Boolean]

  /**
    * Read a file
    */
  def read(path: String): Future[Option[InputStream]]

  /**
    * Read a file
    */
  def readAsString(path: String): Future[Option[String]] //= read(path).map(is => is.mkString)

  /**
    * Read a file
    */
  def readAsIterator(path: String): Future[Option[Iterator[String]]] //= read(path).map(is => is.getLines)

  /**
    * List contents of a directory
    */
  def listContents(directory: String, recursive: Boolean = false): Future[Seq[String]]

  /**
    * Get file's size in KB
    */
  def size(path: String): Future[Long]

  /**
    * Write a new file
    */
  def write(path: String, contents: String): Future[Boolean]= write(path, contents.getBytes.toIterator)

  /**
    * Write a new file using an InputStream
    */
  def write(path: String, contents: Iterator[Byte]): Future[Boolean]

  /**
    * Update an existing file
    */
  def update(path: String, contents: Iterator[Byte]): Future[Boolean]

  /**
    * Update an existing file
    */
  def update(path: String, contents: String): Future[Boolean ]= update(path, contents.getBytes.toIterator)

  /**
    * Rename a file
    */
  def rename(path: String, newPath: String): Future[Boolean]

  /**
    * Copy a file
    */
  def copy(path: String, newPath: String): Future[Boolean]

  /**
    * Delete a file
    */
  def delete(path: String): Future[Boolean]
}
