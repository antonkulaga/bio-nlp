package org.denigma.nlp

import org.denigma.binding.views.BindableView
import org.denigma.nlp.brat._
import org.scalajs.dom.Element
import scalajs.js.JSConverters._

class AnnotatorView(val elem: Element, val connector: WebSocketTransport) extends BindableView {

  protected def initColdData(): ColData = {
    val types =  List( new EntityType("Person", Array("Person", "Per"), "#7fa2ff", "darken") )
    val relationTypes = List(
      new RelationType("Anaphora", List("Anaphora", "Ana"),"3,3", "purple",
        List(
          new RelationRole("Anaphor", List("Person")),
          new RelationRole("Entity", List("Person"))
        )
      )
    )
    val events  = List(
      new EventType("Assassination", List("Assassination", "Assas"), "lightgreen", "darken",
        List(
          new LabeledType("Victim", List("Victim", "Vict")),
          new LabeledType("Perpetrator", List("Perpetrator", "Perp"))
        ))
    )
    val typeAttributes = List(new EntityAttributeType("Notorious", """{ "Notorious": { "glyph": "★" } }"""))
    new ColData(  types, relationTypes, typeAttributes, events)
  }

  protected def initDocData(): DocData = {
    val text = "Ed O'Kelley was the man who shot the man who shot Jesse James."
    val parts = List(
      Entity("T1", "Person", List((0, 11))),
      Entity("T2", "Person", List((20, 23))),
      Entity("T3", "Person", List((37, 40))),
      Entity("T4", "Person", List((50, 61)))
    )
    val atribs =  List(DocAttribute("A1", "Notorious", "T4"))
    val relations = List(Relation("R1", "Anaphora", "Anaphor", "T2", "Entity", "T1"))
    val events = List(
      BratEvent("E1", "T5", List("Perpetrator"->"T3", "Victim"->"T4")),
      BratEvent("E2", "T6", List("Perpetrator"->"T2", "Victim"->"T3"))
    )
    val triggers = List(
      Entity("T5", "Assassination", List((45, 49))),
      Entity("T6", "Assassination", List((28, 32)))
    )
    new DocData( text, parts, atribs, relations, events, triggers)
  }

  override def bindView() = {
    val bratLocation = "/resources/brat"
    val webFontURLs = Array(
      bratLocation + "/static/fonts/Astloch-Bold.ttf",
      bratLocation + "/static/fonts/PT_Sans-Caption-Web-Regular.ttf",
      bratLocation + "/static/fonts/Liberation_Sans-Regular.ttf"
    )
    val colData = initColdData()
    val docData = initDocData()
    BratUtil.embed("annotation", colData, docData, webFontURLs.toJSArray)
  }
}
