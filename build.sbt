organization := "io.onema"

name := "vff"

version := "0.1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= {
  Seq(
    "com.amazonaws"               % "aws-java-sdk-s3"   % "1.11.280",
    "com.github.pathikrit"        %% "better-files"     % "3.4.0",

    // Logging
    "com.typesafe.scala-logging"  %% "scala-logging"    % "3.7.2",
    "ch.qos.logback"              % "logback-classic"   % "1.1.7",

    // Testing
    "org.scalatest"               % "scalatest_2.12"                      % "3.0.4"   % "test",
    "org.scalamock"               % "scalamock-scalatest-support_2.12"    % "3.6.0"   % "test"
  )
}

publishMavenStyle := true
publishTo := Some("Onema Snapshots" at "s3://s3-us-east-1.amazonaws.com/ones-deployment-bucket/snapshots")