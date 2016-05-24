package org.denigma.nlp.brat

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

@JSName("Util")
@js.native
object BratUtil  extends js.Object {

  def embed(id: String = "annotation", collData: ColData, docData: DocData,  webFontURLs: Array[String]): Unit = js.native
}

@ScalaJSDefined
class ColData(val entity_types: Array[EntityType]) extends js.Object

@ScalaJSDefined
class EntityType(val `type`: String, val labels: Array[String], val bgColor: String, val borderColor: String) extends js.Object

@ScalaJSDefined
class DocData(val text: String, val entities: Array[_]) extends js.Object