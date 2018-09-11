import sbt.url

organization := "io.onema"

name := "vff"

version := "0.5.2"

scalaVersion := "2.12.6"

libraryDependencies ++= {
  Seq(
    "com.amazonaws"               % "aws-java-sdk-s3"   % "1.11.280",
    "com.github.pathikrit"        %% "better-files"     % "3.6.0",

    // Logging
    "com.typesafe.scala-logging"  %% "scala-logging"    % "3.7.2",
    "ch.qos.logback"              % "logback-classic"   % "1.1.7",

    // Testing
    "org.scalatest"               % "scalatest_2.12"                      % "3.0.4"   % "test",
    "org.scalamock"               % "scalamock-scalatest-support_2.12"    % "3.6.0"   % "test"
  )
}
// Maven Central Repo boilerplate configuration
pomIncludeRepository := { _ => false }
licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
homepage := Some(url("https://github.com/onema/VFF"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/onema/vff"),
    "scm:git@github.com:onema/VFF.git"
  )
)
developers := List(
  Developer(
    id    = "onema",
    name  = "Juan Manuel Torres",
    email = "software@onema.io",
    url   = url("https://github.com/onema/")
  )
)
publishMavenStyle := true
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
publishArtifact in Test := false
