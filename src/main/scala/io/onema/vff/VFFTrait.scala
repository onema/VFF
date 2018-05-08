/**
  * This file is part of the ONEMA Default (Template) Project Package.
  * For the full copyright and license information,
  * please view the LICENSE file that was distributed
  * with this source code.
  *
  * copyright (c) 2018, Juan Manuel Torres (http://onema.io)
  *
  * @author Juan Manuel Torres <kinojman@gmail.com>
  */

package io.onema.vff

import scala.util.Try

trait VFFTrait {

  //--- Methods ---
  /**
    * Check if the file exists
    */
  def has(path: String): Boolean

  /**
    * Read a file
    */
  def read(path: String): Option[String]

  /**
    * List contents of a directory
    */
  def listContents(directory: String = "", recursive: Boolean = false): Option[Seq[String]]

  /**
    * Get file's size in KB
    */
  def size(path: String): Option[Int]

  /**
    * Write a new file
    */
  def write(path: String, resource: Stream[String], config: Seq[String] = Seq()): Try[Boolean]

  /**
    * Update an existing file
    */
  def update(path: String, resource: Stream[String], config: Seq[String] = Seq()): Try[Boolean]

  /**
    * Rename a file
    */
  def rename(path: String, newPath: String): Try[Boolean]

  /**
    * Copy a file
    */
  def copy(path: String, newPath: String): Try[Boolean]

  /**
    * Delete a file
    */
  def delete(path: String): Boolean

  /**
    * Delete a directory
    */
  def deleteDir(path: String): Boolean

  /**
    * Create or update a file
    */
  def put(path: String, contents: Stream[String], config: Seq[String])

//  def addPlugin(plugin: PluginTrait)
}
