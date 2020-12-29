crossScalaVersions := Seq("2.12.8", "2.11.12", "2.13.3")
scalaVersion := crossScalaVersions.value.last

name := "scala-common-lib"

// also used as a `groupId` by Sonatype
organization := "io.github.ismailfakir"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % "test"
libraryDependencies += "net.liftweb" %% "lift-json" % "3.4.3"



description := "A scala library for common use cases for scala software development"

import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(GitHubHosting("ismailfakir", "scala-common-lib", "md.ismail.fakir@gmail.com"))
// indicate the open source licenses that apply to our project
licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
// publish to the Sonatype repository
dynverSonatypeSnapshots in ThisBuild := true
publishTo := sonatypePublishTo.value

// retrieve secrets to sign files
pgpPublicRing := file("ci/pubring.asc")
pgpSecretRing := file("ci/secring.asc")
pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toArray)

// documentation website
enablePlugins(ParadoxPlugin, ParadoxSitePlugin, TutPlugin, SiteScaladocPlugin, GhpagesPlugin)
tutSourceDirectory := sourceDirectory.value / "documentation"
Paradox / sourceDirectory := tutTargetDirectory.value
makeSite := makeSite.dependsOn(tut).value
SiteScaladoc / siteSubdirName := "api"
paradoxProperties += ("scaladoc.base_url" -> "api")
git.remoteRepo := sonatypeProjectHosting.value.get.scmUrl

// binary compatibility check
mimaPreviousArtifacts := Set.empty // Disabled on `master` branch

publishMavenStyle := true
githubOwner := "ismailfakir"
githubRepository := "scala-common-lib"
githubTokenSource := TokenSource.GitConfig("github.token")