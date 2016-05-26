package org.denigma.nlp.annotator
import org.denigma.brat._
import org.denigma.nlp.messages.Annotations

object ReachBratModel {
  def apply(): ReachBratModel = new ReachBratModel()
}
class ReachBratModel {

  protected def regulation(tp: String, labels: List[String]) =
    EventType(tp, labels,
      List(
      LabeledType("Theme", List("Theme", "Th"))
    ))


  /*
  RelationType(tp, labels,
    roles = List(
      RelationRole("Theme", List("Theme", "Th"))
    )
  )
*/
  protected def geneExpression(tp: String, labels: List[String]) =      EventType(tp, labels,
    List(
      LabeledType("Theme", List("Theme", "Th"))
    )
  )

  protected def bioColData() = {
    val types = List(
      new EntityType("Cellular_component", List("Cellular_component", "CellComp", "CC"), "lightgreen", "darken"),
      new EntityType("Simple_chemical", List("Simple_chemical", "Chemical", "Chem"), "pink", "darken"),
      new EntityType("Site", List("Site", "Si"), "gold", "darken"),
      new EntityType("Gene_or_gene_product", List("Gene_or_gene_product", "Gene_GP", "GGP"), "blue", "darken"),
      new EntityType("Protein", List("Protein", "Pro", "P"), "violet", "darken"),
      new EntityType("Complex", List("Protein_Complex", "Complex", "Cplx"), "navy", "darken")
    )
    /*
    * type: Origin
  args: [ Target, Rulename ]

  type: Negation
  args: none (attribute present or absent)

  type: Speculation
  args: none (attribute present or absent)

    * */
    val regulationTypes: List[EventType] = List(
      regulation("Regulation", List("Regulation", "Regulat", "Reg")),
      regulation("Positive_regulation", List("Positive_regulation", "+Regulation", "+Reg")),
      regulation("Activation", List("Activation", "Activ", "+Act")),
      regulation("Negative_regulation", List("Negative_regulation", "-Regulation", "-Reg")),
      regulation("Inactivation", List("Inactivation", "Inactiv", "-Act"))
    )

    val geneExpressions: List[EventType] = List(
      geneExpression("Gene_expression", List("Gene_expression", "Gene_expr", "GeneX")),
      geneExpression("Transcription", List("Transcription", "Transcript", "Tsc")),
      geneExpression("Translation", List("Translation", "Translate", "Tsl"))
    )

    val conversions: List[EventType] = List(

    )
    val eventTypes = regulationTypes ++ geneExpressions ++conversions
    ColData(types, events = eventTypes)

  }
/*
  protected def eventMention(idmention: Annotations.CorefEventMention, ids: Map[Annotations.Mention, String]): Entity = {

  }
*/

  def docData(doc: Annotations.Document, mentions: Map[Annotations.Mention, String] ): DocData =  {
    val els = mentions.map{
      case (mention, id) =>
        val pos = doc.position(mention)
        val ent = Entity(id, mention.label, List(pos))
        ent
    }.toList
    DocData(doc.fullText,elements = els)
  }

  lazy val colData: ColData = bioColData()
}
