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

package io.onema.vff

import io.onema.vff.adapter.{Adapter, AwsS3Adapter, Local}

object FileSystem {
  def apply(): FileSystem = new FileSystem(new Local)
  def s3(bucketName: String): FileSystem = new FileSystem(AwsS3Adapter(bucketName))
}

class FileSystem(val adapter: Adapter) {
  /**
    * Check if the file exists
    */
  def has(path: String): Boolean = adapter.has(path)

  /**
    * Read a file
    */
  def read(path: String): Option[String] = adapter.read(path)

  /**
    * Get an Iterator of strings. Equivalent to get lines in a Source
    */
  def readStream(path: String): Iterator[String] = adapter.readStream(path)

  /**
    * List contents of a directory
    */
  def listContents(directory: String, recursive: Boolean): Seq[String] = adapter.listContents(directory, recursive)

  /**
    * Get file's size in KB
    */
  def size(path: String): Long = adapter.size(path)

  /**
    * Write a new file
    */
  def write(path: String, contents: String): Boolean = adapter.write(path, contents)

  /**
    * Write a new file
    */
  def write(path: String, contents: Iterator[String]): Boolean = adapter.write(path, contents)

  /**
    * Update an existing file
    */
  def update(path: String, resource: String): Boolean = adapter.update(path, resource)

  /**
    * Rename a file
    */
  def rename(path: String, newPath: String): Boolean = adapter.rename(path, newPath)

  /**
    * Copy a file
    */
  def copy(path: String, newPath: String): Boolean = adapter.copy(path, newPath)

  /**
    * Delete a file
    */
  def delete(path: String): Boolean = adapter.delete(path)
}
