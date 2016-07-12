import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.gzip.Import.gzip
import com.typesafe.sbt.web.SbtWeb.autoImport._
import com.typesafe.sbt.web.pipeline.Pipeline
import com.typesafe.sbt.web.{PathMapping, SbtWeb}
import net.lullabyte.ChromeSbtPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPluginInternal
import playscalajs.PlayScalaJS
import sbt.Keys._
import sbt._
import spray.revolver.RevolverPlugin.autoImport._

lazy val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug

lazy val publishSettings = Seq(
  bintrayRepository := "denigma-releases",

  bintrayOrganization := Some("denigma"),

  licenses += ("MPL-2.0", url("http://opensource.org/licenses/MPL-2.0")),

  bintrayPublishIvyStyle := true
)

/**
 * For parts of the project that we will not publish
 */
lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)


//settings for all the projects
lazy val commonSettings = Seq(
  scalaVersion := Versions.scala,
  organization := "org.denigma",
  scalacOptions ++= Seq( "-feature", "-language:_" ),
  // Enable JAR export for staging
  exportJars := true,
  parallelExecution in Test := false,
  resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"), //for scala-js-binding
  resolvers += Resolver.jcenterRepo,
  unmanagedClasspath in Compile <++= unmanagedResources in Compile,
  libraryDependencies ++= Dependencies.commonShared.value ++ Dependencies.testing.value,
  updateOptions := updateOptions.value.withCachedResolution(true) //to speed up dependency resolution
) ++ eclipseSettings


val scalaJSDevStage  = Def.taskKey[Pipeline.Stage]("Apply fastOptJS on all Scala.js projects")

def scalaJSDevTaskStage: Def.Initialize[Task[Pipeline.Stage]] = Def.task { mappings: Seq[PathMapping] =>
  mappings ++ PlayScalaJS.devFiles(Compile).value ++ PlayScalaJS.sourcemapScalaFiles(fastOptJS).value
}

lazy val brat = (project in file("brat"))
  .settings(commonSettings ++ publishSettings: _*)
  .settings(
    name := "brat-facade",
    version := Versions.bratFacade,
    scalaVersion := Versions.scala,
    libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % Versions.dom
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay)

lazy val annotator = crossProject
  .crossType(CrossType.Full)
  .in(file("annotator"))
  .settings(commonSettings ++ publishSettings: _*)
  .settings(
    name := "annotator",
    version := Versions.bioNLP
  )
  .disablePlugins(RevolverPlugin)
  .jsSettings(
    libraryDependencies ++= Dependencies.sjsLibs.value,
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    jsDependencies += RuntimeDOM % Test
    //jsEnv in Test := new org.scalajs.jsenv.selenium.SeleniumJSEnv(org.scalajs.jsenv.selenium.Firefox)
  )
  .jsConfigure(p=>p.enablePlugins(ScalaJSPlay).dependsOn(brat))
  .jvmSettings(
    (emitSourceMaps in fullOptJS) := true,
    dependencyOverrides += "org.biopax.paxtools" % "paxtools-core" % Versions.paxtools
  )

lazy val chromeTarget = SettingKey[File]("chromeTarget",
  "Target for chrome files")

lazy val chromeUnpacked = SettingKey[File]("chromeUnpacked",
  "Unpacked chrome application")

lazy val annotatorJS = annotator.js
lazy val annotatorJVM = annotator.jvm

lazy val chromeBio = (project in file("chrome-bio"))
  .settings(commonSettings:_*)
  .disablePlugins(RevolverPlugin)
  .enablePlugins(SbtWeb)
  .enablePlugins(ChromeSbtPlugin)
  .dependsOn(annotatorJS)
  .settings(
    name := "chrome-bio",
    version := Versions.bioNLP,
    scalacOptions ++= Seq(
      "-language:implicitConversions",
      "-language:existentials",
      "-Xlint",
      "-deprecation",
      "-Xfatal-warnings",
      "-feature"
    ),
    (chromePackageContent in Compile) := (resourceDirectory in Assets).value,
    (chromeTarget in Compile) := target.value / "chrome",
    (chromeUnpacked in Compile) := (chromeTarget in Compile).value / "unpacked",
    persistLauncher := true,
    persistLauncher in Test := false,
    relativeSourceMaps := true,
    libraryDependencies ++= Seq(
      "net.lullabyte" %%% "scala-js-chrome" % Versions.scalaJsChrome withSources() withJavadoc(),
      "org.webjars" % "Semantic-UI" %  Versions.semanticUI
    ),
    (managedClasspath in Runtime) += (packageBin in Assets).value,
    skip in packageJSDependencies := false,
    chromeGenerateManifest := {
      (chromePackageContent in Compile).value / "manifest.json"
    },
    chromeBuildFast := {
      val tg = (chromeTarget in Compile).value
      val unpacked = (chromeUnpacked in Compile).value
      val webjars =  (WebKeys.webJarsDirectory in Assets).value
      IO.createDirectory(unpacked)
      val content: File = (chromePackageContent in Compile).value
      val manifest = (chromeGenerateManifest in Compile).value
      val jsLib = (fastOptJS in Compile).value.data
      val jsDeps = (packageJSDependencies in Compile).value
      val jsLauncher = (packageScalaJSLauncher in Compile).value.data
      IO.copyDirectory(webjars, unpacked, overwrite = true, preserveLastModified = true)
      IO.copyDirectory(content, unpacked, overwrite = true, preserveLastModified = true)
      val forCopy = List(
        (jsLib, unpacked / "main.js"),
        (jsDeps, unpacked / "deps.js"),
        (jsLauncher, unpacked / "launcher.js"),
        (manifest, unpacked / "manifest.json")
      )
      IO.copy(forCopy)
      unpacked
    },
    chromePackage := {
      val out = (chromeTarget in Compile).value
      val chromeAppDir = chromeBuildFast.value
      val zipFile = new File(out, s"${name.value}.zip")
      IO.zip(allSubpaths(chromeAppDir), zipFile)
      zipFile
    }
  )

lazy val app = crossProject
  .crossType(CrossType.Full)
  .in(file("app"))
  .settings(commonSettings ++ publishSettings: _*)
  .settings(
    name := "bio-nlp",
    version := Versions.bioNLP
  ).dependsOn(annotator)
  .disablePlugins(RevolverPlugin).
    // adding the `it` configuration
    configs(IntegrationTest).
    // adding `it` tasks
    settings(Defaults.itSettings:_*).
    // add `shared` folder to `jvm` source directories
    jvmSettings(unmanagedSourceDirectories in IntegrationTest ++=
    CrossType.Full.sharedSrcDir(baseDirectory.value, "it").toSeq).
    // add `shared` folder to `js` source directories
    jsSettings(unmanagedSourceDirectories in IntegrationTest ++=
    CrossType.Full.sharedSrcDir(baseDirectory.value, "it").toSeq).
  // adding ScalaJSClassLoader to `js` configuration
  jsSettings(inConfig(IntegrationTest)(ScalaJSPluginInternal.scalaJSTestSettings):_*)
  .jsSettings(
    libraryDependencies ++= Dependencies.sjsLibs.value,
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    jsDependencies += RuntimeDOM % Test
    //jsEnv in Test := new org.scalajs.jsenv.selenium.SeleniumJSEnv(org.scalajs.jsenv.selenium.Firefox)
  )
  .jsConfigure(p=>p.enablePlugins(ScalaJSPlay))
  .jvmSettings(
    libraryDependencies ++= Dependencies.akka.value ++ Dependencies.webjars.value,
    mainClass in Compile := Some("org.denigma.nlp.Main"),
    libraryDependencies ++= Dependencies.compilers.value ++ Dependencies.otherJvm.value,
    scalaJSDevStage := scalaJSDevTaskStage.value,
    (emitSourceMaps in fullOptJS) := true,
    pipelineStages in Assets := {
      val stages = sys.env.get("APP_MODE") match {
        case Some(str) if str.toLowerCase.startsWith("prod") =>
          println("PROJECT IS IN PRODUCTION MODE")
          Seq(scalaJSProd, gzip)
        case other =>
          println("PROJECT IS IN DEVELOPMENT MODE")
          Seq(scalaJSDevStage, gzip)
      }
      stages
    },
    libraryDependencies += "com.lihaoyi" %% "ammonite-ops" % Versions.ammonite,
    libraryDependencies += "com.lihaoyi" %% "ammonite-shell" % Versions.ammonite,
    dependencyOverrides += "org.biopax.paxtools" % "paxtools-core" % Versions.paxtools,
    initialCommands in (Test, console) := Console.out
  )
  .jvmConfigure(p => p.enablePlugins(SbtTwirl, SbtWeb, PlayScalaJS))

lazy val appJS = app.js
lazy val appJVM = app.jvm settings (scalaJSProjects := Seq(appJS))

lazy val root = Project("root",file("."),settings = commonSettings)
  .settings(
    name := "bio-nlp",
    version := Versions.bioNLP,
    mainClass in Compile := (mainClass in appJVM in Compile).value,
    (fullClasspath in Runtime) += (packageBin in appJVM in Assets).value,
    maintainer := "Anton Kulaga <antonkulaga@gmail.com>",
    packageSummary := "bio-nlp",
    packageDescription := """BIO NLP akka-http service for nuggets extraction""",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint", "-J-Xss5M"),
    javaOptions in reStart += "-Xmx6g",
    initialCommands in (Test, console) := Console.out,
    debugSettings := Some(spray.revolver.DebugSettings(5005, suspend = false))
  ) dependsOn appJVM aggregate(appJVM, appJS) enablePlugins JavaServerAppPackaging
