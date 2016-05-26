package org.denigma.nlp.extractions

import java.nio.ByteBuffer

import akka.actor.{Actor, ActorLogging}
import better.files.File
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import org.denigma.nlp.communication.WorkMessages
import org.denigma.nlp.messages._

import scala.language.implicitConversions

class NLPActor(config: Config, files: File) extends Actor with ActorLogging{

  lazy val cacheFile = files / "anno.binary"

  val filePath: String = config.as[Option[String]]("app.files").getOrElse("files/")

  lazy val extractor = new BioExtractor(config.getConfig("nlp"), filePath)

  val converter = new MentionConverter

  override def receive: Receive = {
    case WorkMessages.AskStatus(from) =>
      println("tell them that I am ready")
      from ! WorkMessages.ReachReady(true)

    case MessagesNLP.Annotate(text) =>
      println("annotation received with text = "+text)
      annotate(text)
      //cacheSend()

  }

  protected def annotate(text: String) = {

    val (doc, mentions) = extractor.annotate(text)
    val sentences: Vector[Annotations.Sentence] = doc.sentences.map(converter.sentence2annotation).toVector

    val document = Annotations.Document(doc.id.getOrElse(""), sentences, doc.text)
    val mens = mentions.map(m=>converter.convert(m)).toList
    val message = MessagesNLP.DocumentAnnotations(document, mens)
    save(message)
    sender ! message
  }

  protected def cacheSend() = {
    val message = loadFromCache()
    val doc = message.document
    val mentions = message.mentions
    val offsets = message.document.sentencesOffsets
    sender ! message
  }

  protected def loadFromCache() = {
    import boopickle.DefaultBasic._
    import org.denigma.nlp.messages._
    val bytes = cacheFile.loadBytes
    val message = Unpickle[MessagesNLP.Message].fromBytes(ByteBuffer.wrap(bytes))
    message.asInstanceOf[MessagesNLP.DocumentAnnotations]
  }


  protected def save(anno: MessagesNLP.DocumentAnnotations) = {
    import java.io.{File => JFile}

    import boopickle.DefaultBasic._
    import org.denigma.nlp.messages._
    val d: ByteBuffer = Pickle.intoBytes[MessagesNLP.Message](anno)
    cacheFile.createIfNotExists()
    cacheFile.write(d.array())(better.files.File.OpenOptions.default)
    println("message was successfuly save!")
  }
}
