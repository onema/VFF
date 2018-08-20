Virtual File Framework (VFF)
============================
![CodeBuild Badge](https://codebuild.us-east-1.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoidm0yYnR4Nys0emNZZSsrU0M4eGdIK0NsVkVDS2tnWTFmdFVONERrS20vUmMwcndzVVpCdVBGZngwa2JwaUN4WnlMUWlZd0RlanZLb243V20rd0pvRVNnPSIsIml2UGFyYW1ldGVyU3BlYyI6Ind5M3ZFK2xzcXh6MUZ5eVEiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3e8dec3ddf8b4728b7e1cebae9fba8e6)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=onema/VFF&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/3e8dec3ddf8b4728b7e1cebae9fba8e6)](https://www.codacy.com?utm_source=github.com&utm_medium=referral&utm_content=onema/VFF&utm_campaign=Badge_Coverage)

A File System abstraction for scala inspired by [FlySystem](https://flysystem.thephpleague.com/docs/).

Currently two adapters are provided:
* Local
* S3

## Getting started

### Install
```
libraryDependencies += "io.onema" % "vff_2.12" % "0.3.0"
```

### Create a file system object
```scala
import io.onema.vff.FileSystem
import io.onema.vff.adapter.{Local, AwsS3Adapter}

// Local file system
val localfs = new FileSystem(new Local)

// S3 file system
val s3fs = new FileSystem(AwsS3Adapter("bucket-name"))
```
### Reading a file
All read operations return an `Option`:

#### `read`
```scala
import io.onema.vff.extensions.StreamExtension._
val data: Option[InputStream] = localfs.read("path/to/file.txt")

// Using the InputStream extensions you can quickly convert it to other types
val str: String = data.mkString
val byteIter: Iterator[Byte] = data.toBytes
val byteArray: Array[Byte] = data.toByteArray
val lines: Iterator[String] = data.getLines
```
Other convenience methods are provided to read the data as a string or as an iterator of strings

#### `readAsString`
```scala
val strOption: Option[String] = localfs.readAsString("path/to/file.txt")
```

#### `readAsIterator`
```scala
val iterOption: Option[Iterator[String]] = localfs.readAsIterator("path/to/file.txt")
```

### Creating a file

VFF uses byte iterators to write to the destination

#### `write`
```scala
import scala.io.Source
import io.onema.vff.extensions.StreamExtension._

// If you have an input stream you can convert it using the stream extensions
localfs.read("path/to/image0.png").foreach((image: InputStream) => {
  val result = localfs.write("path/to/image1.png", image.toBytes)
  val copyResult = localfs.copy("path/to/image1.png","path/to/image1.png")
})

// A convenience method is included to write strings directly
localfs.write("path/to/file2.txt", "Some text")
``` 
