/**
  * This file is part of the ONEMA VFF Package.
  * For the full copyright and license information,
  * please view the LICENSE file that was distributed
  * with this source code.
  *
  * copyright (c) 2018, Juan Manuel Torres (http://onema.io)
  *
  * @author Juan Manuel Torres <kinojman@gmail.com>
  */

package io.onema.vff.extensions

import io.onema.vff.extensions.StringExtensions.TrimString
import org.scalatest.{FlatSpec, Matchers}


class StringExtensionTest extends FlatSpec with Matchers {
  "A path starting with a single '/'" should "be trimmed" in {
    // Arrange
    val lsingle = "/foo.txt"
    val rsingle = "foo/"

    // Act
    val lresult = lsingle.ltrim
    val rresult = rsingle.rtrim

    // Assert
    lresult should be ("foo.txt")
    rresult should be ("foo")
  }

//  "A path string starting with multiple '/'" should "be trimmed" in {
//    // Arrange
//    val lmultiple = "///foo.txt"
//    val rmultiple = "foo///"
//
//    // Act
//    val lresult = lmultiple.ltrim
//    val rresult = rmultiple.rtrim
//
//    // Assert
//    lresult should be ("foo.txt")
//    rresult should be ("foo")
//  }
}
