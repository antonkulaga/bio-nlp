package org.denigma.nlp.annotator
import org.denigma.brat._
import org.denigma.nlp.messages.Annotations
import org.scalajs.dom

import scalajs.js.JSConverters._
import scala.scalajs.js
import scala.scalajs.js.Array

object ReachBratModel {
  def apply(): ReachBratModel = new ReachBratModel()
}
class ReachBratModel {

  protected def regulation(tp: String, labels: List[String]) =
    EventType(tp, labels,
      List(
      LabeledType("Theme", List("Theme", "Th"))
    ))

  protected def geneExpression(tp: String, labels: List[String]) =      EventType(tp, labels,
    List(
      LabeledType("Theme", List("Theme", "Th"))
    )
  )

  protected def justEvent(tp: String, labels: List[String]) = EventType(tp, labels,
    List(
      LabeledType("Theme", List("Theme", "Th"))
    )
  )

  protected def eventWithCauseAndSite(tp: String, labels: List[String]) = EventType(tp, labels,
    List(
      LabeledType("Theme", List("Theme", "Th")),
      LabeledType("Cause", List("Cause", "Ca")),
      LabeledType("Site", List("Site", "Si"))
    )
  )

  protected def eventWithSite(tp: String, labels: List[String]) = EventType(tp, labels,
    List(
      LabeledType("Theme", List("Theme", "Th")),
      LabeledType("Site", List("Site", "Si"))
    )
  )

  protected def eventWithProduct(tp: String, labels: List[String]) = EventType(tp, labels,
    List(
      LabeledType("Theme", List("Theme", "Th")),
      LabeledType("Product", List("Product", "Prod", "Pr"))
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

    val attributes = List[EntityAttributeType]( //TODO: fix FORMAT
      new EntityAttributeType("Origin", Map("Target"->"Rulename")),
      new EntityAttributeType("Negation", Map.empty),
      new EntityAttributeType("Speculation", Map.empty)
    )
    /*
    * type: Origin
  args: [ Target, Rulename ]

  type: Negation
  args: none (attribute present or absent)

  type: Speculation
  args: none (attribute present or absent)

    * */
    val relations = List[RelationType](
      RelationType("Equiv", List("Equivalent", "Equiv", "Eq"))
    )

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
      eventWithProduct("Conversion", List("Conversion", "Conver", "Conv")),
      eventWithCauseAndSite("Phosphorylation", List("Phosphorylation", "Phosphor", "+Phos")),
      eventWithCauseAndSite("Dephosphorylation", List("Dephosphorylation", "Dephos", "-Phos")),
      eventWithCauseAndSite("Acetylation", List("Acetylation", "Acetyl", "+Acet")),
      eventWithCauseAndSite("Deacetylation", List("Deacetylation", "Deacetyl", "-Acet")),
      eventWithCauseAndSite("Glycosylation", List("Glycosylation", "Glycosyl", "+Glycos")),
      eventWithCauseAndSite("Deglycosylation", List("Deglycosylation", "Deglycos", "-Glycos")),
      eventWithCauseAndSite("Hydroxylation", List("Hydroxylation", "Hydroxyl", "+Hydr")),
      eventWithCauseAndSite("Dehydroxylation", List("Dehydroxylation", "Dehydrox", "-Hydr")),
      eventWithCauseAndSite("Methylation", List("Methylation", "Methyl", "+Meth")),
      eventWithCauseAndSite("Demethylation", List("Demethylation", "Demethyl", "-Meth")),
      eventWithCauseAndSite("Ubiquitination", List("Ubiquitination", "Ubiquit", "+Ubiq")),
      eventWithCauseAndSite("Deubiquitination", List("Deubiquitination", "Deubiq", "-Ubiq")),
      eventWithCauseAndSite("DNA_methylation", List("DNA_methylation", "DNA_methyl", "+DNAmeth")),
      eventWithCauseAndSite("DNA_demethylation", List("DNA_demethylation", "DNA_demeth", "-DNAmeth")),
      justEvent("Degradation", List("Degradation", "Degrade", "Deg")),
      eventWithCauseAndSite("Binding", List("Binding", "Bind")),
      eventWithProduct("Dissociation", List("Dissociation", "Dissoc")),
      EventType("Localization", List("Localization", "Local", "Loc"),
        List(
          LabeledType("Theme", List("Theme", "Th")),
          LabeledType("AtLoc", List("AtLoc", "At")),
          LabeledType("FromLoc", List("FromLoc", "From")),
          LabeledType("ToLoc", List("ToLoc", "To"))
        )),
      eventWithSite("Protein_with_site", List("Protein_with_site", "Pro_w_site", "PWS")),
      EventType("Transport", List("Transport"),
        List(
          LabeledType("Theme", List("Theme", "Th")),
          LabeledType("FromLoc", List("FromLoc", "From")),
          LabeledType("ToLoc", List("ToLoc", "To"))
        ))
    )
    val eventTypes = regulationTypes ++ geneExpressions ++conversions
    ColData(types, relationTypes = relations, attributes = attributes, events = eventTypes)
  }

  protected def eventMention(doc: Annotations.Document, mention: Annotations.CorefEventMention, id: String, mentions: Map[Annotations.Mention, String]): BratEvent = {
    val trig = mentions.getOrElse(mention.trigger, {
      dom.console.error("cannot find mention for trigger " + mention.trigger)
      ""
    })
    val args = mention.arguments.flatMap{
      case (key, vals) =>
        vals.collect{
          case v if mentions.contains(v) =>
            key -> mentions(v)
        }
    }.toList
    //println("ARGUMENTS ARE: "+args)
    BratEvent(id, trig, args)
  }

  protected def entityMention(doc: Annotations.Document, mention: Annotations.Mention, id: String, mentions: Map[Annotations.Mention, String]): Entity = {
    val pos = mention.span//doc.position(mention)
    val ent = Entity(id, mention.label, List(pos.start -> pos.end))
    ent
  }

  def docData(doc: Annotations.Document, mentions: Map[Annotations.Mention, String] ): DocData =  {
    //val byIds = mentions.map{ case (key, value)=>(value, key)}
    val (
      entities: Map[String, Entity],
      triggers: Map[String, Entity],
      events: Map[String, BratEvent]
      ) = mentions.foldLeft((
      Map.empty[String, Entity],
      Map.empty[String, Entity],
      Map.empty[String, BratEvent])
    ){

      case ((ents, trigs, evs), (mention: Annotations.CorefEventMention, id)) if evs.contains(id) => (ents, trigs, evs)

      case ((ents, trigs, evs), (mention: Annotations.CorefEventMention, id)) =>
        val ev = eventMention(doc, mention, id, mentions)
        if(trigs.contains(ev.trigger)) (ents, trigs, evs.updated(id, ev)) else
        if(ents.contains(ev.trigger))(ents, trigs.updated(ev.trigger, ents(ev.trigger)), evs.updated(id, ev)) else
        {
          val tr = entityMention(doc, mention, ev.trigger, mentions)
          (ents, trigs.updated(ev.trigger, tr), evs.updated(id, ev))
        }

      case ((ents, trigs, evs), (mention, id)) if trigs.contains(id) =>
        //(ents.updated(id, trigs(id)), trigs, evs)
        (ents, trigs, evs)

      case ((ents, trigs, evs), (mention, id)) =>
        val ent = entityMention(doc, mention, id, mentions)
        (ents.updated(id, ent), trigs, evs)

    }
    /*
    println("++++++++++++++++++++++++++++++++++++")
    pprint.pprintln(entities)
    println("####################################")
    pprint.pprintln(triggers)
    println("####################################")
    pprint.pprintln(events)
    println("====================================")
    */
    val (entList, trigList, eventList) = (entities.values.toList, triggers.values.toList, events.values.toList)

    //val all: List[BratObject] = entList++trigList++eventList

    val comments = mentions.map(m=>js.Array(m._2, m._1.foundBy,m._1.label)).toList

    DocData(doc.text, entities = entList, trigs = trigList, events = eventList, comms = comments)
  }

  lazy val colData: ColData = bioColData()
}
