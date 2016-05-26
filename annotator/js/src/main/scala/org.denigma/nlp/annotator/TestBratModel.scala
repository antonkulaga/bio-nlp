package org.denigma.nlp.annotator
import org.denigma.brat._

class TestBratModel {
  protected def initColData(): ColData = {
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
      new EventType("Assassination", List("Assassination", "Assas"),
        List(
          new LabeledType("Victim", List("Victim", "Vict")),
          new LabeledType("Perpetrator", List("Perpetrator", "Perp"))
        ), "lightgreen", "darken")
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

  val colData = initColData()
  val docData = initDocData()

}
