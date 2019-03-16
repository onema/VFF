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

package io.onema.vff

import java.io._

import io.onema.vff.adapter.AdapterAsync

import scala.concurrent.Future


class FileSystemAsync(val adapter: AdapterAsync) {
  /**
    * Check if the file exists
    */
  def has(path: String): Future[Boolean] = adapter.has(path)

  /**
    * Read a file
    */
  def read(path: String): Future[Option[InputStream]] = adapter.read(path)

  /**
    * Get an Iterator of strings. Equivalent to get lines in a Source
    */
  def readAsIterator(path: String): Future[Option[Iterator[String]]] = adapter.readAsIterator(path)

  /**
    * Get an Iterator of strings. Equivalent to get lines in a Source
    */
  def readAsString(path: String): Future[Option[String]] = adapter.readAsString(path)

  /**
    * List contents of a directory
    */
  def listContents(directory: String, recursive: Boolean = false): Future[Seq[String]] = adapter.listContents(directory, recursive)

  /**
    * Get file's size in KB
    */
  def size(path: String): Future[Long] = adapter.size(path)

  /**
    * Write a new file
    */
  def write(path: String, contents: String): Future[Boolean] = adapter.write(path, contents)

  /**
    * Write a new file
    */
  def write(path: String, contents: Iterator[Byte]): Future[Boolean] = adapter.write(path, contents)

  /**
    * Update an existing file
    */
  def update(path: String, resource: Iterator[Byte]): Future[Boolean] = adapter.update(path, resource)

  /**
    * Update an existing file
    */
  def update(path: String, resource: String): Future[Boolean] = adapter.update(path, resource)

  /**
    * Rename a file
    */
  def rename(path: String, newPath: String): Future[Boolean] = adapter.rename(path, newPath)

  /**
    * Copy a file
    */
  def copy(path: String, newPath: String): Future[Boolean] = adapter.copy(path, newPath)

  /**
    * Delete a file
    */
  def delete(path: String): Future[Boolean] = adapter.delete(path)
}
