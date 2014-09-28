import BuildSettings._
import Dependencies._

releaseSettings

Publishing.settings

Revolver.settings


scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8", "-language", "postfixOps")


lazy val root = (
    Project(
      id = "atomizer-root",
      base = file(".")
    )
    settings(BuildSettings.globalSettings: _*)
    aggregate(
      atomModel
    )
)

lazy val atomModel = (
  module("atom-model", Seq(T.scalacheck, C.eclipseLink, T.scalaXml))
  settings(
    testOptions in Test += Tests.Argument("-oD")
  )
  settings(
    net.virtualvoid.sbt.graph.Plugin.graphSettings: _*
  )
)
