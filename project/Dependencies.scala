import sbt._
import Keys._

object Dependencies {

  object V {
    val scalaTest = "2.2.1"
    val spray = "1.3.1"
    val akka = "2.3.5"
    val jackson = "2.4.1"
  }

  object C {
    val sprayHttp = "io.spray" %% "spray-http" % V.spray
    val sprayCan = "io.spray" %% "spray-can" % V.spray
    val sprayRouting = "io.spray" %% "spray-routing" % V.spray
    val akka = "com.typesafe.akka" %% "akka-actor" % V.akka
    val nScalaTime = "com.github.nscala-time" %% "nscala-time" % "1.2.0"
    val eclipseLink = "org.eclipse.persistence" % "org.eclipse.persistence.moxy" % "2.5.2"
  }

  object T {
    val sprayClient = "io.spray" %% "spray-client" % V.spray % "test"
    val scalaTest = "org.scalatest" %% "scalatest" % V.scalaTest % "test"
    val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % V.akka % "test"
    val scalacheck = "org.scalacheck" %% "scalacheck" % "1.11.5" % "test"
    val scalaXml = "org.scala-lang.modules" %% "scala-xml" % "1.0.2" % "test"
  }

  val globalDependencies = Seq(
    C.akka,
    T.scalaTest,
    T.akkaTestKit,
    C.sprayHttp,
    C.nScalaTime
  )

  val globalSettings = Seq(
    resolvers += "spray repo" at "http://repo.spray.io",
    libraryDependencies := globalDependencies
  )
}
