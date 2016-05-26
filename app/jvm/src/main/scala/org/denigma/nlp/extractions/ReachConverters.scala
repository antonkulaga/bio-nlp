package org.denigma.nlp.extractions

import edu.arizona.sista.odin._
import edu.arizona.sista.processors.Sentence
import edu.arizona.sista.reach.grounding._
import edu.arizona.sista.reach.mentions._
import edu.arizona.sista.struct.Interval
import org.denigma.nlp.messages._

class MentionConverter extends ReachMentionsConverters {

}
trait ReachElementsConverters {
  protected implicit def unwrapArray[T](arr: Option[Array[T]]): List[T] = arr.map(t => t.toList).getOrElse(Nil)

  protected implicit def unwrapSeq[T](col: Option[Seq[T]]): List[T] = col.map(t => t.toList).getOrElse(Nil)

  protected implicit def unwrapMap[T, U](col: Option[Map[T, U]]): Map[T, U] = col.getOrElse(Map.empty[T, U])

  protected implicit def KBEntry2annotationEntry(entry: KBEntry): Annotations.KBEntry = {
    Annotations.KBEntry(entry.id, entry.text, entry.key, entry.namespace, entry.species)
  }

  protected implicit def KBMetaInfo2Map(info: KBMetaInfo): collection.immutable.Map[String, String] = {
    info.toMap
  }

  protected implicit def interval2Annotation(interval: Interval): Annotations.Interval = {
    println(interval)
    Annotations.Interval(interval.start, interval.end)
  }



  implicit def sentence2annotation(s: Sentence): Annotations.Sentence = {
    Annotations.Sentence(
      s.words.toList,
      s.startOffsets.toList,
      s.endOffsets.toList,
      s.tags,
      s.lemmas,
      s.entities,
      s.norms,
      s.chunks
    )
  }
}
trait ReachMentionsConverters extends ReachElementsConverters{


  protected implicit def modification2Annotation(mod: Modification): Annotations.Modification = mod match {
    case  PTM(label: String, evidence, site )=> Annotations.PTM(label, evidence.map(convert))

    case EventSite(site) => Annotations.EventSite(convert(site))

    case Negation(evidence) => Annotations.Negation(evidence)

    case Hypothesis(evidence)  => Annotations.Hypothesis(evidence)

    case Mutant(evidence: Mention, foundBy: String)=> Annotations.Mutant(evidence, foundBy)
  }

 protected implicit def argument2Annotation(args: Map[String, Seq[Mention]]): Map[String, Seq[Annotations.Mention]] = {
    val result: Map[String, Seq[Annotations.Mention]] = args.mapValues{ case mens => mens.collect{case m: BioMention => convert(m)}}
    result
  }

  implicit def convert(mention: Mention): Annotations.Mention = mention match {
    case m: CorefEventMention => corefEventMention2annotation(m)
    case m: CorefTextBoundMention => corefTextBoundMention2annotation(m)
    case m: CorefRelationMention => corefRelation2annotation(m)
    case m: TextBoundMention => textBoundSimple2Annotation(m)

    case other => throw new Exception("Unknown mention type: "+mention)

  }

  protected implicit def resolution2annotationResolution(resol: KBResolution): Annotations.KBResolution = {
    val inf: Map[String, String] = resol.metaInfo.map(KBMetaInfo2Map)
    Annotations.KBResolution(resol.entry, inf)
  }

  protected def corefRelation2annotation(mention: CorefRelationMention): Annotations.CorefRelationMention = {
    val resolutions: List[Annotations.KBResolution] = mention.candidates().map(
      c => c.toList.map(resolution2annotationResolution))
      .getOrElse(List.empty)
    val cont: Map[String, Seq[String]] = mention.context
    val gr: Option[Annotations.KBResolution] = mention.grounding().map(resolution2annotationResolution)
    //val trig = textBoundMention2annotation(mention.trigger)
    val mods: Set[Annotations.Modification] =  mention.modifications.map(modification2Annotation)
    Annotations. CorefRelationMention(
      label = mention.displayLabel,
      labels = mention.labels.toList,
      foundBy = mention.foundBy,
      arguments = mention.arguments,
      modifications = mods,
      context = cont,
      detMap = mention.detMap,
      headMap = mention.headMap,
      grounding = gr,
      candidates = resolutions,
      tokenInterval = interval2Annotation(mention.tokenInterval),
      sentenceNum = mention.sentence
    )
  }

  protected def corefEventMention2annotation(mention: CorefEventMention): Annotations.CorefEventMention = {
    val resolutions: List[Annotations.KBResolution] = mention.candidates().map(
      c => c.toList.map(resolution2annotationResolution))
      .getOrElse(List.empty)
    val cont: Map[String, Seq[String]] = mention.context
    val gr: Option[Annotations.KBResolution] = mention.grounding().map(resolution2annotationResolution)
    val trig = textBoundMention2annotation(mention.trigger)
    val mods: Set[Annotations.Modification] =  mention.modifications.map(modification2Annotation)
    Annotations.CorefEventMention(
      label = mention.displayLabel,
      labels = mention.labels.toList,
      foundBy = mention.foundBy,
      arguments = mention.arguments,
      modifications = mods,
      context = cont,
      trigger = trig,
      isDirect = mention.isDirect,
      detMap = mention.detMap,
      headMap = mention.headMap,
      grounding = gr,
      candidates = resolutions,
      tokenInterval = interval2Annotation(mention.tokenInterval),
      sentenceNum = mention.sentence
    )
  }

  protected def textBoundSimple2Annotation(mention: TextBoundMention) = {
    //val mods: Set[Annotations.Modification] =  other.modifications.map(modification2Annotation)
    Annotations.SimpleTextBoundMention(
      label = mention.label,
      labels = mention.labels.toList,
      foundBy = mention.foundBy,
      arguments = mention.arguments,
      tokenInterval = interval2Annotation(mention.tokenInterval),
      sentenceNum = mention.sentence
    )
  }

  protected def textBoundMention2annotation(mention: TextBoundMention): Annotations.TextBoundMention = mention match {
    case tb: CorefTextBoundMention => corefTextBoundMention2annotation(tb)
    case other => textBoundSimple2Annotation(other)
  }


  protected def corefTextBoundMention2annotation(mention: CorefTextBoundMention): Annotations.CorefTextBoundMention = {
    val resolutions: List[Annotations.KBResolution] = mention.candidates().map(
      c => c.toList.map(resolution2annotationResolution))
      .getOrElse(List.empty)
    val mods: Set[Annotations.Modification] =  mention.modifications.map(modification2Annotation)
    Annotations.CorefTextBoundMention(
      label = mention.displayLabel,
      labels = mention.labels.toList,
      foundBy = mention.foundBy,
      arguments = mention.arguments,
      modifications = mods,
      context = mention.context,
      detMap = mention.detMap,
      headMap = mention.headMap,
      grounding = mention.grounding().map(resolution2annotationResolution),
      candidates = resolutions,
      tokenInterval = interval2Annotation(mention.tokenInterval),
      sentenceNum = mention.sentence
    )
  }

}