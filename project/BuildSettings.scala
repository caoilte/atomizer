import sbt._
import sbt.Keys._

object BuildSettings {

  val globalSettings = Seq(
    organization := "org.caoilte",
    scalaVersion := "2.11.2",
    scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8", "-target:jvm-1.7")
  )

  def module(name: String, dependencies: Seq[ModuleID] = Seq()) = (
    Project(s"atomizer-$name", file(name))
    settings(globalSettings: _*)
    settings(Dependencies.globalSettings: _*)
    settings(libraryDependencies ++= dependencies)
  )

}
