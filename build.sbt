import sbt.Keys.{homepage, scmInfo}
import sbt.url

lazy val scala213 = "2.13.0"
lazy val scala212 = "2.12.8"
lazy val scala211 = "2.11.12"
lazy val supportedScalaVersions = List(scala213, scala212, scala211)

ThisBuild / organization := "io.onema"
ThisBuild / version      := "0.6.1"
ThisBuild / scalaVersion := scala212
ThisBuild / parallelExecution in Test := false

val awsSdkVersion = "1.11.515"
val extensionsVersion = "0.2.1"

lazy val vff = (project in file("."))
  .settings(skip in publish := true)
  .aggregate(vffCore, vffS3Adapter, vffTest)
publishArtifact in vff := false

lazy val vffCore = (project in file("vff"))
  .settings(
    name := "vff",
    commonPublishSettings,
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= {
      Seq(
        "io.onema"             %% "stream-extensions" % extensionsVersion,
        "io.onema"             %% "string-extensions" % extensionsVersion,
        "com.github.pathikrit" %% "better-files"      % "3.8.0",

        // Logging
        "com.typesafe.scala-logging"  %% "scala-logging"    % "3.9.2",
      )
    }
  )

lazy val vffS3Adapter = (project in file("vff-s3-adapter"))
  .settings(
    name := "vff-s3-adapter",
    commonPublishSettings,
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= {
      Seq(
       "com.amazonaws"        % "aws-java-sdk-s3"   % awsSdkVersion
      )
    }
  ).dependsOn(vffCore)

lazy val vffTest = (project in file("vff-test"))
  .settings(
    name := "vff-test",
    publishTo := Some(Resolver.file("unused repo", file("foo/bar"))),
    publishArtifact := false,
    libraryDependencies ++= {
      Seq(
        // Testing
        "org.scalatest"               % "scalatest_2.12"                      % "3.0.5"   % Test,
        "org.scalamock"               %% "scalamock"                          % "4.1.0"   % Test
      )
    }
  ).dependsOn(vffS3Adapter, vffCore)

// Maven Central Repo boilerplate configuration
pomIncludeRepository := { _ => false }

developers := List(
  Developer(
    id    = "onema",
    name  = "Juan Manuel Torres",
    email = "software@onema.io",
    url   = url("https://github.com/onema/")
  )
)

// Maven Central Repo boilerplate configuration
lazy val commonPublishSettings = Seq(
  //  publishTo := Some("Onema Snapshots" at "s3://s3-us-east-1.amazonaws.com/ones-deployment-bucket/snapshots"),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },
  licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
  homepage := Some(url("https://github.com/onema/VFF")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/onema/vff"),
      "scm:git@github.com:onema/VFF.git"
    )
  ),
  developers := List(
    Developer(
      id    = "onema",
      name  = "Juan Manuel Torres",
      email = "software@onema.io",
      url   = url("https://github.com/onema/")
    )
  ),
  publishArtifact in Test := false
)