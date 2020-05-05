enablePlugins(WorkbenchPlugin)

lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    inThisBuild(List(
      version      := "0.1-SNAPSHOT",
      scalaVersion := "2.11.11"
    )),
    name := "Dama",
    libraryDependencies ++= Seq(
      "org.scala-js"             %%% "scalajs-dom" % "1.0.0",
      "com.thoughtworks.binding" %%% "binding"     % "10.0.2",
      "com.thoughtworks.binding" %%% "dom"         % "10.0.2",
      "org.scalatest" %%% "scalatest" % "3.0.0" % "test",
      "org.scalactic" %% "scalactic" % "3.0.0"
    ),
    addCompilerPlugin(
      "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
    ),
    scalaJSUseMainModuleInitializer := true/*,
    testFrameworks += new TestFramework("utest.runner.Framework")*/
  )

/*val cross = new utest.jsrunner.JsCrossBuild(
  // Cross-platform settings
  organization := "com.lihaoyi",
  name := "demo",
  version := "0.1"
)*/


