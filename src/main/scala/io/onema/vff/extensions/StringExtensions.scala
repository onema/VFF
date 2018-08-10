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

package io.onema.vff.extensions

object StringExtensions {
  implicit class TrimString(path: String) {
    def ltrim: String = path.stripPrefix("/").trim
    def rtrim: String = path.stripSuffix("/").trim
  }
}
