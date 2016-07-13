object Versions extends WebJarsVersions with ScalaJSVersions with SharedVersions with OtherJVM
{
	val scala = "2.11.8"

	val bioNLP = "0.0.5"

	val binding = "0.8.12"

	val bindingControls = "0.0.19"

	val betterFiles = "2.16.0"

	val reach = "1.3.0"

	val scalaJsChrome = "0.2.1"

}

trait OtherJVM {

	val bcrypt = "2.4"

	val ammonite = "0.6.2"

	val apacheCodec = "1.10"

	val akkaHttpExtensions = "0.0.13"

	val retry = "0.2.1"

	val macroParadise = "2.1.0"

	val logback = "1.1.7"

	val akka = "2.4.8"

	val circeHttp = "1.7.0"

	val libSBOLj = "2.1.0"

	val ficus: String = "1.2.6"

	val paxtools = "4.3.1"

}


trait ScalaJSVersions {

	val dom = "0.9.1"

	val codemirrorFacade = "5.13.2-0.7"

	val threejsFacade = "0.0.74-0.1.7"

	val d3jsFacade = "0.3.1"

	val bratFacade = "1.3-0.0.1"

}

//versions for libs that are shared between client and server
trait SharedVersions
{

	val circe = "0.4.1"

	val scalaTags = "0.5.5"

	val scalaCSS = "0.4.1"

	val scalaTest = "3.0.0-RC4"//"3.0.0-SNAP13"

	val fastparse = "0.3.7"

	val quicklens = "1.4.7"

	val pprint =  "0.4.1"
}


trait WebJarsVersions{

	val jquery =  "2.2.4"//"3.0.0"

	val jqueryUI = "1.11.4"

	val jquerySVG = "1.5.0"

	val semanticUI = "2.2"

	val codemirror = "5.13.2"

	val threeJS = "r74"

	val webcomponents = "0.7.12"

	val d3js: String = "3.5.12"

	val malihuScrollBar: String = "3.1.5"

}

