package org.denigma.nlp.extractions

import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import org.denigma.nlp.communication.WorkMessages
import org.denigma.nlp.messages._

import scala.language.implicitConversions

class NLPActor(config: Config) extends Actor with ActorLogging{

  val filePath: String = config.as[Option[String]]("app.files").getOrElse("files/")

  val extractor = new BioExtractor(config.getConfig("nlp"), filePath)

  val converter = new MentionConverter



  override def receive: Receive = {
    case WorkMessages.AskStatus(from) =>
      println("tell them that I am ready")
      from ! WorkMessages.ReachReady(true)

    case MessagesNLP.Annotate(text) =>
      println("annotation received with text = "+text)
      val (doc, mentions) = extractor.annotate(text)
      val sentences: List[Annotations.Sentence] = doc.sentences.map(converter.sentence2annotation).toList

      val document = Annotations.Document(doc.id.getOrElse(""), sentences, doc.text)
      val mens = mentions.map(m=>converter.convert(m)).toList
      sender ! MessagesNLP.DocumentAnnotations(document, mens)

  }
}
