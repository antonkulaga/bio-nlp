package org.denigma.nlp.extractions

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.Config
import better.files.File.OpenOptions
import better.files._
import com.typesafe.config.Config
import edu.arizona.sista.processors.Sentence
import edu.arizona.sista.reach.mentions.BioMention
import net.ceedubs.ficus.Ficus._
import org.denigma.nlp.communication.WorkMessages
import org.denigma.nlp.messages._

import scala.language.implicitConversions

trait ReachConverters {
  protected implicit def unwrap[T](arr: Option[Array[T]]): List[T] = arr.map(t=>t.toList).getOrElse(Nil)
  protected implicit def unwrap[T](col: Option[Seq[T]]): List[T] = col.map(t=>t.toList).getOrElse(Nil)
  protected implicit def unwrap[T, U](col: Option[Map[T, U]]): Map[T, U] = col.getOrElse(Map.empty[T, U])


  protected implicit def sentence2annotation(s: Sentence): Annotations.Sentence = {
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

  protected implicit def biomention2annotation(mention: BioMention) = {
    Annotations.BioMention(
      displayLabel = mention.displayLabel,
      labels = mention.labels.toList,
      context = mention.context)
    mention.foundBy
  }

}

class NLPActor(config: Config) extends Actor with ActorLogging with ReachConverters{

  val filePath: String = config.as[Option[String]]("app.files").getOrElse("files/")

  val extractor = new BioExtractor(config.getConfig("nlp"), filePath)



  override def receive: Receive = {
    case WorkMessages.AskStatus(from) =>
      println("tell them that I am ready")
      from ! WorkMessages.ReachReady(true)

    case MessagesNLP.Annotate(text) =>
      println("annotation received with text = "+text)
      val (doc, mentions) = extractor.annotate(text)
      val sentences: List[Annotations.Sentence] = doc.sentences.map(sentence2annotation).toList

      val document = Annotations.Document(doc.id.getOrElse(""), sentences, doc.text)
      sender ! Annotations.DocumentAnnotations(document)

  }
}
