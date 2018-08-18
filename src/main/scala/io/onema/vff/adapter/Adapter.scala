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

trait Adapter {

  /**
    * Check if the file exists
    */
  def has(path: String): Boolean

  /**
    * Read a file
    */
  def read(path: String): Option[String]

  /**
    * Read a file and return a stream
    */
  def readStream(path: String): Iterator[String]

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
  def write(path: String, contents: String): Boolean

  /**
    * Write a new file using an iterator
    */
  def write(path: String, contents: Iterator[String]): Boolean

  /**
    * Update an existing file
    */
  def update(path: String, contents: String): Boolean

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
