lazy val root = project
  .in(file(""))
  .settings(
    name := "functional-data-modelling",
    version := "0.1.0",
    scalacOptions ++= Seq(
      "-language:postfixOps",
      "-Xfatal-warnings"
    ),
    scalaVersion := "2.13.3"
  )

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
