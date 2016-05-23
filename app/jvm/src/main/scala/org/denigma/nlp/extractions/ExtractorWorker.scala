package org.denigma.nlp.extractions

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.Config
import org.denigma.nlp.MessagesNLP

import better.files.File.OpenOptions
import better.files._
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

class ExtractorWorker(config: Config) extends Actor with ActorLogging{

  val filePath: String = config.as[Option[String]]("app.files").getOrElse("files/")

  val bio = new BioExtractor(config.getConfig("nlp"), filePath)

  override def receive: Receive = {
    case MessagesNLP.Annotate(text) =>
  }
}
