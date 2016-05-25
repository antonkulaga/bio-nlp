package org.denigma.nlp.messages.Annotations

import boopickle.CompositePickler

object Mention {
  import boopickle.DefaultBasic._
  implicit val mentionPickler: CompositePickler[Mention] = compositePickler[Mention]
    .addConcreteType[CorefTextBoundMention]
    .addConcreteType[SimpleTextBoundMention]
    .join(TextBoundMention.pickler)
    /*
    .addConcreteType[CorefEventMention]
    .addConcreteType[CorefRelationMention]
    */

}

object CorefEventMention {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[CorefEventMention] = PicklerGenerator.generatePickler[CorefEventMention]

}
case class CorefEventMention(
                              label: String,
                              labels: List[String],
                              foundBy: String,
                              arguments: Map[String, Seq[Mention]],
                              modifications: Set[Modification],
                              context: Map[String, Seq[String]],
                              trigger: TextBoundMention,
                              isDirect: Boolean = false,
                              detMap: Map[String, Int] = Map.empty,
                              headMap: Map[String, Int] = Map.empty,
                              grounding: Option[KBResolution] = None,
                              candidates: List[KBResolution] = List.empty,
                              tokenInterval: Interval = Interval.empty,
                              sentenceNum: Int = 0
                            ) extends BioMention with TextBoundMention with Anaphoric



object SimpleTextBoundMention {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[SimpleTextBoundMention] = PicklerGenerator.generatePickler[SimpleTextBoundMention]
}

case class SimpleTextBoundMention (
                                    label: String,
                                    labels: List[String],
                                    foundBy: String,
                                    arguments: Map[String, Seq[Mention]],
                                    headMap: Map[String, Int] = Map.empty,
                                    tokenInterval: Interval = Interval.empty,
                                    sentenceNum: Int = 0
                                  ) extends TextBoundMention



object CorefRelationMention  {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[CorefRelationMention] = PicklerGenerator.generatePickler[CorefRelationMention]
}

case class CorefRelationMention(  label: String, //
                                  labels: List[String],
                                  foundBy: String,
                                  arguments: Map[String, Seq[Mention]],
                                  modifications: Set[Modification],
                                  context: Map[String, Seq[String]],
                                  detMap: Map[String, Int] = Map.empty,
                                  headMap: Map[String, Int] = Map.empty,
                                  grounding: Option[KBResolution] = None,
                                  candidates: List[KBResolution] = List.empty,
                                  tokenInterval: Interval = Interval.empty,
                                  sentenceNum: Int = 0
                               ) extends  BioMention


object CorefTextBoundMention {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[CorefTextBoundMention] = PicklerGenerator.generatePickler[CorefTextBoundMention]
}
case class CorefTextBoundMention(
                                  label: String,
                                  labels: List[String],
                                  foundBy: String,
                                  arguments: Map[String, Seq[Mention]],
                                  modifications: Set[Modification],
                                  context: Map[String, Seq[String]],
                                  detMap: Map[String, Int] = Map.empty,
                                  headMap: Map[String, Int] = Map.empty,
                                  grounding: Option[KBResolution] = None,
                                  candidates: List[KBResolution] = List.empty,
                                  tokenInterval: Interval = Interval.empty,
                                  sentenceNum: Int = 0
                                ) extends TextBoundMention with BioMention


  trait BioMention extends Mention with Grounding with Anaphoric
  {
    def label: String
    def labels: List[String]
    def foundBy: String
    def context: Map[String, Seq[String]]
    def tokenInterval: Interval
    def sentenceNum: Int
    def modifications: Set[Modification]
  }

  object TextBoundMention {
    import boopickle.DefaultBasic._

    implicit val pickler: CompositePickler[TextBoundMention] = compositePickler[TextBoundMention]
      .addConcreteType[CorefTextBoundMention]
      .addConcreteType[SimpleTextBoundMention]
  }

  trait TextBoundMention extends Mention

  trait Mention
  {
    def label: String
    def labels: List[String]
    def tokenInterval: Interval
    def sentenceNum: Int
    def arguments: Map[String, Seq[Mention]]

    def foundBy: String

    /** index of the first token in the mention */
    def start: Int = tokenInterval.start

    /** one after the last token in the mention */
    def end: Int = tokenInterval.end

    /** returns true if this is a valid mention */
    def isValid: Boolean = true

    /** returns true if the string matches any of the mention labels */
    def matches(label: String): Boolean = labels contains label

  }

  trait Anaphoric{
    self: Mention=>
    def detMap: Map[String, Int]
    def headMap: Map[String, Int]
  }


  sealed trait Grounding {
    self: Mention=>

    def grounding: Option[KBResolution]
    def candidates: List[KBResolution]
    def bestPick = grounding.orElse(candidates.headOption)
  }
