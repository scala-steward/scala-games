
name := "scala-games"
version := "0.0.1"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

scalaVersion := "2.12.7"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",   // source files are in UTF-8
  "-deprecation",         // warn about use of deprecated APIs
  "-unchecked",           // warn about unchecked type parameters
  "-feature",             // warn about misused language features
  "-language:higherKinds",// allow higher kinded types without `import scala.language.higherKinds`
  "-Xlint",               // enable handy linter warnings
  "-Xfatal-warnings",     // turn compiler warnings into errors
  "-Ypartial-unification", // allow the compiler to unify type constructors of different arities
  "-Yrangepos"
)

libraryDependencies += "org.typelevel" %% "cats-core" % "2.1.1"
libraryDependencies += "org.typelevel" %% "cats-effect" % "1.4.0"
libraryDependencies += "com.softwaremill.sttp" %% "core" % "1.7.2"
libraryDependencies += "org.specs2" %% "specs2-core" % "4.9.3" % "test"
libraryDependencies += "org.specs2" %% "specs2-scalacheck" % "4.9.3" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.3" % "test"

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.0")
