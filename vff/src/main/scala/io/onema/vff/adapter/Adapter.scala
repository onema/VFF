/**
  * This file is part of the ONEMA Default (Template) Project Package.
  * For the full copyright and license information,
  * please view the LICENSE file that was distributed
  * with this source code.
  *
  * copyright (c) 2018 - 2019,Juan Manuel Torres (http://onema.io)
  *
  * @author Juan Manuel Torres <software@onema.io>
  */

package io.onema.vff.adapter

import io.onema.extensions.StreamExtensions._
import java.io._

trait Adapter {

  /**
    * Check if the file exists
    */
  def has(path: String): Boolean

  /**
    * Read a file
    */
  def read(path: String): Option[InputStream]

  /**
    * Read a file
    */
  def readAsString(path: String): Option[String] = read(path).map(is => is.mkString)

  /**
    * Read a file
    */
  def readAsIterator(path: String): Option[Iterator[String]] = read(path).map(is => is.getLines)

  /**
    * List contents of a directory
    */
  def listContents(directory: String, recursive: Boolean = false): Seq[String]

  /**
    * Get file's size in KB
    */
  def size(path: String): Long

  /**
    * Write a new file
    */
  def write(path: String, contents: String): Boolean = write(path, contents.getBytes.toIterator)

  /**
    * Write a new file using an InputStream
    */
  def write(path: String, contents: Iterator[Byte]): Boolean

  /**
    * Update an existing file
    */
  def update(path: String, contents: Iterator[Byte]): Boolean

  /**
    * Update an existing file
    */
  def update(path: String, contents: String): Boolean = update(path, contents.getBytes.toIterator)

  /**
    * Rename a file
    */
  def rename(path: String, newPath: String): Boolean

  /**
    * Copy a file
    */
  def copy(path: String, newPath: String): Boolean

  /**
    * Delete a file
    */
  def delete(path: String): Boolean
}
