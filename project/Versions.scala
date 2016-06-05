object Versions extends WebJarsVersions with ScalaJSVersions with SharedVersions with OtherJVM
{
	val scala = "2.11.8"

	val bioNLP = "0.0.3"

	val binding = "0.8.8"

	val bindingControls = "0.0.16"

	val betterFiles = "2.16.0"

	val reach = "1.2.3"

}

trait OtherJVM {

	val bcrypt = "2.4"

	val ammonite = "0.5.8"

	val apacheCodec = "1.10"

	val akkaHttpExtensions = "0.0.12"

	val retry = "0.2.1"

	val macroParadise = "2.1.0"

	val logback = "1.1.7"

	val akka = "2.4.7"

	val circeHttp = "1.7.0"

	val libSBOLj = "2.1.0"

	val breeze = "0.12"

	val ficus: String = "1.2.6"

	val paxtools = "4.3.1"

}


trait ScalaJSVersions {

	val jqueryFacade = "0.11" //1.0-RC2

	val dom = "0.9.0"

	val codemirrorFacade = "5.11-0.7"

	val threejsFacade = "0.0.74-0.1.7"

	val d3jsFacade = "0.3.1"

	val bratFacade = "1.3-0.0.1"

}

//versions for libs that are shared between client and server
trait SharedVersions
{

	val circe = "0.4.1"

	val scalaTags = "0.5.4"

	val scalaCSS = "0.4.1"

	val productCollections = "1.4.2"

	val scalaTest = "3.0.0-RC1"//"3.0.0-SNAP13"

	val scalaTestMatchers = "3.0.0-SNAP13"

	val fastparse = "0.3.7"

	val quicklens = "1.4.7"

	val pprint =  "0.4.1"
}


trait WebJarsVersions{

	val jquery =  "2.2.4"

	val jqueryUI = "1.11.4"

	val jquerySVG = "1.5.0"

	val semanticUI = "2.1.8"

	val codemirror = "5.13.2"

	val threeJS = "r74"

	val webcomponents = "0.7.12"

	val d3js: String = "3.5.12"


}

