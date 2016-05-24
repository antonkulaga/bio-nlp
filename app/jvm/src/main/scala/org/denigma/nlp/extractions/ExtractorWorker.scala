package org.denigma.nlp.extractions

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.Config
import better.files.File.OpenOptions
import better.files._
import com.typesafe.config.Config
import edu.arizona.sista.processors.Sentence
import net.ceedubs.ficus.Ficus._
import org.denigma.nlp.messages.{Annotations, MessagesNLP}

import scala.language.implicitConversions

class ExtractorWorker(config: Config) extends Actor with ActorLogging{

  val filePath: String = config.as[Option[String]]("app.files").getOrElse("files/")

  val extractor = new BioExtractor(config.getConfig("nlp"), filePath)

  protected implicit def unwrap[T](arr: Option[Array[T]]): List[T] = arr.map(t=>t.toList).getOrElse(Nil)

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

  override def receive: Receive = {
    case MessagesNLP.Annotate(text) =>
      println("annotation received with text = "+text)
      val (doc, mentions) = extractor.annotate(text)
      val sentences: List[Annotations.Sentence] = doc.sentences.map(sentence2annotation).toList

      val document = Annotations.Document(doc.id.getOrElse(""), sentences, doc.text)
      sender ! Annotations.DocumentAnnotations(document)

  }
}
