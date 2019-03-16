Virtual File Framework (VFF)
============================
[![CodeBuild Badge](https://codebuild.us-east-1.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoidktrcVFjOVJsUmR2K0dFdm9pRmhzSmZ3NVIzTG9RVW5oKzBqOHlSSVkwRWxCY0lEM1ZtdENScHEvWUZKMGJmZnlER2pqZWsyemdrK2tpRGtPdWowOTJVPSIsIml2UGFyYW1ldGVyU3BlYyI6InF5RmdJZEZWRmhNb21hQzUiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)](https://console.aws.amazon.com/codebuild/home?region=us-east-1#/projects/VirtualFF/view)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3e8dec3ddf8b4728b7e1cebae9fba8e6)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=onema/VFF&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/3e8dec3ddf8b4728b7e1cebae9fba8e6)](https://www.codacy.com?utm_source=github.com&utm_medium=referral&utm_content=onema/VFF&utm_campaign=Badge_Coverage)
[![LICENSE](https://img.shields.io/badge/license-Apache--2.0-blue.svg?longCache=true&style=flat-square)](LICENSE)

A File System abstraction for scala inspired by [FlySystem](https://flysystem.thephpleague.com/docs/).

Currently two adapters are provided:
* Local (included with VFF)
* S3 (as of version 0.6.0 must be installed separately `"io.onema" % "vff-s3-adapter_2.12" % "VERSION"`)

## Getting started

### Install
for the latest version please visit [maven.org](https://search.maven.org/search?q=a:vff_2.12)
```
libraryDependencies += "io.onema" % "vff_2.12" % "LATEST_VERSION"
```

### Create a file system object
```scala
import io.onema.vff.FileSystem
import io.onema.vff.adapter.{Local, AwsS3Adapter}

// Local file system
val fs = new FileSystem(new Local)

// S3 file system
val s3fs = new FileSystem(AwsS3Adapter("bucket-name"))
```
### Reading a file
All read operations return an `Option`:

#### `read`
```scala
val data: Option[InputStream] = fs.read("path/to/file.txt")
val str: String = data.mkString
```
Other convenience methods are provided to read the data as a string or as an iterator of strings

#### `readAsString`
```scala
val strOption: Option[String] = fs.readAsString("path/to/file.txt")
```

#### `readAsIterator`
```scala
val iterOption: Option[Iterator[String]] = fs.readAsIterator("path/to/file.txt")
```

### Creating a file

VFF uses byte iterators to write to the destination

#### `write`
```scala
import scala.io.Source
import io.onema.vff.extensions.StreamExtension._

// If you have an input stream you can convert it using the stream extensions
fs.read("path/to/image0.png").foreach((image: InputStream) => {
  val result = fs.write("path/to/image1.png", image.toBytes)
  val copyResult = fs.copy("path/to/image1.png","path/to/image1.png")
})

// A convenience method is included to write strings directly
fs.write("path/to/file2.txt", "Some text")
``` 

### Extension Methods
#### `InputStream`
Using the `InputStream` methods and extensions provided in the  you can quickly convert the `data` returned by the 
`read` method into other types

```scala
import io.onema.extensions.StreamExtension._

val data = fs.read("path/to/file.txt")
val str: String = data.mkString
val byteIter: Iterator[Byte] = data.toBytes
val byteArray: Array[Byte] = data.toByteArray
val lines: Iterator[String] = data.getLines
```

## Async API 
As of version `0.6.0` an Async file system is provided, the method names remain the same but all calls async and return a scala Future `Future[ReturnType]`.
Implementations for each of the adapters are provided. 

Example:

```scala
import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration._
import io.onema.vff.FileSystemAsync

    // Arrange
    val fs = FileSystemAsync()
    val uuid = UUID.randomUUID().toString
    val dir = s"/tmp/$uuid"
    
    // create a single file, this will not be part of the test
    println(dir)
    Await.result(fs.write(s"$dir/tmp", "0"), 500.millis)

    // Act
    val results: Seq[Boolean] = (0 to 100000).map { i =>
      fs.write(s"$dir/$i", i.toString)
    }.map(Await.result(_, 5000.millis))

    // Assert
    results.foreach(x => {
      x should be (true)
    })
```
