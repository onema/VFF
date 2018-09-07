Virtual File Framework (VFF)
============================
[![CodeBuild Badge](https://codebuild.us-east-1.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoicG5UTVFwZ3JHUzFuS1ZidGxMeDJwT09pTldHMWZndG5CcTc0S0dBcFZjYlVSL0ZVdGdkUHREelVXbG0yV1RYbDNLdlhJZFk2S1J4N1RkcW1yRkpWbWQ4PSIsIml2UGFyYW1ldGVyU3BlYyI6IkpUL1FUY2dSMkM3bktWNmUiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)](https://console.aws.amazon.com/codebuild/home?region=us-east-1#/projects/vff/view)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3e8dec3ddf8b4728b7e1cebae9fba8e6)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=onema/VFF&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/3e8dec3ddf8b4728b7e1cebae9fba8e6)](https://www.codacy.com?utm_source=github.com&utm_medium=referral&utm_content=onema/VFF&utm_campaign=Badge_Coverage)
[![LICENSE](https://img.shields.io/badge/license-Apache--2.0-blue.svg?longCache=true&style=flat-square)](LICENSE)

A File System abstraction for scala inspired by [FlySystem](https://flysystem.thephpleague.com/docs/).

Currently two adapters are provided:
* Local
* S3

## Getting started

### Install
for the latest version please visit [maven.org](https://search.maven.org/search?q=a:vff_2.12)
```
libraryDependencies += "io.onema" % "vff_2.12" % "0.5.0"
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
import io.onema.vff.extensions.StreamExtension._

val data = fs.read("path/to/file.txt")
val str: String = data.mkString
val byteIter: Iterator[Byte] = data.toBytes
val byteArray: Array[Byte] = data.toByteArray
val lines: Iterator[String] = data.getLines
```
