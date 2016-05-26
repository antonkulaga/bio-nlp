package org.denigma.nlp.extractions

import edu.arizona.sista.odin._
import edu.arizona.sista.processors.Sentence
import edu.arizona.sista.reach.grounding._
import edu.arizona.sista.reach.mentions._
import edu.arizona.sista.struct.Interval
import org.denigma.nlp.messages._

import scala.collection.mutable

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


  protected def modification2Annotation(mod: Modification, converted: mutable.Map[Mention, Annotations.Mention]): Annotations.Modification = mod match {
    case  PTM(label: String, evidence, site )=> Annotations.PTM(label, evidence.map(ev=>convert(ev, converted)))

    case EventSite(site) => Annotations.EventSite(convert(site, converted))

    case Negation(evidence) => Annotations.Negation(convert(evidence, converted))

    case Hypothesis(evidence)  => Annotations.Hypothesis(convert(evidence, converted))

    case Mutant(evidence: Mention, foundBy: String)=> Annotations.Mutant(convert(evidence, converted), foundBy)
  }

 protected  def argument2Annotation(args: Map[String, Seq[Mention]], converted: mutable.Map[Mention, Annotations.Mention]): Map[String, Seq[Annotations.Mention]] = {
    val result: Map[String, Seq[Annotations.Mention]] = args.mapValues{ case mens => mens.collect{case m: BioMention => convert(m, converted)}}
    result
  }

  implicit def convert(mention: Mention, converted: mutable.Map[Mention, Annotations.Mention]): Annotations.Mention = mention match {
    case m if converted.contains(m) => converted(m)
    case m: CorefTextBoundMention => corefTextBoundMention2annotation(m, converted)
    case m: CorefEventMention => corefEventMention2annotation(m, converted)
    case m: CorefRelationMention => corefRelation2annotation(m, converted)
    case m: TextBoundMention => textBoundSimple2Annotation(m, converted)
    case other => throw new Exception("Unknown mention type: "+mention)
  }

  def convert(mentions: Seq[Mention]): List[Annotations.Mention] = {
    val cache = mutable.Map.empty[Mention, Annotations.Mention]
    mentions.map(m=>convert(m, cache)).toList
  }

  protected implicit def resolution2annotationResolution(resol: KBResolution): Annotations.KBResolution = {
    val inf: Map[String, String] = resol.metaInfo.map(KBMetaInfo2Map)
    Annotations.KBResolution(resol.entry, inf)
  }

  protected def corefRelation2annotation(mention: CorefRelationMention, converted: mutable.Map[Mention, Annotations.Mention]): Annotations.CorefRelationMention = {
    val resolutions: List[Annotations.KBResolution] = mention.candidates().map(
      c => c.toList.map(resolution2annotationResolution))
      .getOrElse(List.empty)
    val cont: Map[String, Seq[String]] = mention.context
    val gr: Option[Annotations.KBResolution] = mention.grounding().map(resolution2annotationResolution)
    val mods: Set[Annotations.Modification] =  mention.modifications.map(m=>modification2Annotation(m, converted))
    Annotations. CorefRelationMention(
      label = mention.displayLabel,
      labels = mention.labels.toList,
      foundBy = mention.foundBy,
      arguments = argument2Annotation(mention.arguments, converted),
      modifications = mods,
      context = cont,
      grounding = gr,
      candidates = resolutions,
      tokenInterval = interval2Annotation(mention.tokenInterval),
      sentenceNum = mention.sentence,
      sentence = mention.sentenceObj
    )
  }

  protected def corefEventMention2annotation(mention: CorefEventMention, converted: mutable.Map[Mention, Annotations.Mention]): Annotations.CorefEventMention = {
    val resolutions: List[Annotations.KBResolution] = mention.candidates().map(
      c => c.toList.map(resolution2annotationResolution))
      .getOrElse(List.empty)
    val cont: Map[String, Seq[String]] = mention.context
    val gr: Option[Annotations.KBResolution] = mention.grounding().map(resolution2annotationResolution)
    val trig = textBoundMention2annotation(mention.trigger, converted)
    val mods: Set[Annotations.Modification] =  mention.modifications.map(m=>modification2Annotation(m, converted))
    Annotations.CorefEventMention(
      label = mention.displayLabel,
      labels = mention.labels.toList,
      foundBy = mention.foundBy,
      arguments = argument2Annotation(mention.arguments, converted),
      modifications = mods,
      context = cont,
      trigger = trig,
      isDirect = mention.isDirect,
      grounding = gr,
      candidates = resolutions,
      tokenInterval = interval2Annotation(mention.tokenInterval),
      sentenceNum = mention.sentence,
      sentence = mention.sentenceObj
    )
  }

  protected def textBoundSimple2Annotation(mention: TextBoundMention, converted: mutable.Map[Mention, Annotations.Mention]) = {
    //val mods: Set[Annotations.Modification] =  other.modifications.map(modification2Annotation)
    Annotations.SimpleTextBoundMention(
      label = mention.label,
      labels = mention.labels.toList,
      foundBy = mention.foundBy,
      arguments = argument2Annotation(mention.arguments, converted),
      tokenInterval = interval2Annotation(mention.tokenInterval),
      sentenceNum = mention.sentence,
      sentence = mention.sentenceObj
    )
  }

  protected def textBoundMention2annotation(mention: TextBoundMention, converted: mutable.Map[Mention, Annotations.Mention]): Annotations.TextBoundMention = mention match {
    case m if converted.contains(m) => converted(m).asInstanceOf[Annotations.TextBoundMention] //TODO check this unsafe cast
    case tb: CorefTextBoundMention => corefTextBoundMention2annotation(tb, converted)
    case other => textBoundSimple2Annotation(other, converted)
  }


  protected def corefTextBoundMention2annotation(mention: CorefTextBoundMention, converted: mutable.Map[Mention, Annotations.Mention]): Annotations.CorefTextBoundMention = {
    val resolutions: List[Annotations.KBResolution] = mention.candidates().map(
      c => c.toList.map(resolution2annotationResolution))
      .getOrElse(List.empty)
    val mods: Set[Annotations.Modification] =  mention.modifications.map(m=>modification2Annotation(m, converted))
    Annotations.CorefTextBoundMention(
      label = mention.displayLabel,
      labels = mention.labels.toList,
      foundBy = mention.foundBy,
      arguments = argument2Annotation(mention.arguments, converted),
      modifications = mods,
      context = mention.context,
      grounding = mention.grounding().map(resolution2annotationResolution),
      candidates = resolutions,
      tokenInterval = interval2Annotation(mention.tokenInterval),
      sentenceNum = mention.sentence,
      sentence = mention.sentenceObj
    )
  }

}