import play.sbt.routes.RoutesKeys

name := "test"

version := "1.0"

lazy val `test` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

scalaVersion := "2.13.4"

RoutesKeys.routesImport ++= Seq("config.Binders._", "java.time.LocalDate")

val quickbooksOnlineVersion = "6.0.7"
val quilVersion = "3.6.1"
// platform libraries
libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice)


// intuit platforms
libraryDependencies ++= Seq(
  "com.intuit.quickbooks-online" % "ipp-v3-java-data",
  "com.intuit.quickbooks-online" % "ipp-v3-java-devkit",
  "com.intuit.quickbooks-online" % "oauth2-platform-api"
).map(_ % quickbooksOnlineVersion)

// 3-rd parties
libraryDependencies ++= Seq(
  "io.getquill" %% "quill-sql"
).map(_ % quilVersion)

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-lang3" % "3.11"
)

unmanagedResourceDirectories in Test += baseDirectory.value / "target/web/public/test"