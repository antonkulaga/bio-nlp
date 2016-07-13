package org.denigma.nlp.communication

import java.io.{File => JFile}

import akka.actor.ActorRef
import akka.http.scaladsl.model.ws.{BinaryMessage, TextMessage}
import akka.stream.actor.ActorPublisherMessage
import akka.util.ByteString
import boopickle.DefaultBasic._
import org.denigma.nlp.messages.MessagesNLP.KeepAlive
import org.denigma.nlp.messages._
import scala.concurrent.duration._

import scala.util.{Failure, Success}

class UserActor(val username: String, nlp: ActorRef) extends Messenger
{

  protected def onTextMessage: Receive = {
    case SocketMessages.IncomingMessage(channel, uname, TextMessage.Strict(text), time) =>
  }

  protected def annotationMessages: Receive  = {
    case a @ MessagesNLP.Annotate(text) =>
      println("ANNOTATION GOES TO USER with text = "+text)
      nlp ! a
  }

  protected def otherMessages: Receive  = {
    case other => log.error(s"unexpected $other")
  }

  def onMessageNLP = {
    annotationMessages.orElse(otherMessages)
  }

  protected def onBinaryMessage: Receive = {
    case SocketMessages.IncomingMessage(channel, uname, message: BinaryMessage.Strict, time) =>
      val mes = Unpickle[MessagesNLP.Message].fromBytes(message.data.toByteBuffer)
      onMessageNLP(mes)

    case SocketMessages.IncomingMessage(channel, uname, message: BinaryMessage.Streamed, time) =>
      //message.dataStream.runWi
      val bytes = message.dataStream.runReduce[ByteString]{
        case (a, b) => a ++ b
      }
      bytes.onComplete{
        case Failure(th)=>
          log.error("Binary streaming failed")

        case Success(data) =>
          log.info("Binary streaming received!")
          val mes = Unpickle[MessagesNLP.Message].fromBytes(data.toByteBuffer)
          onMessageNLP(mes)
      }
    //log.error(s"something binary received on $channel by $username")
  }

  protected def onServerMessage: Receive = {

    case s : MessagesNLP.ServerErrors=>
      val d = Pickle.intoBytes[MessagesNLP.Message](s)
      send(d)

    case WorkMessages.ReachReady(true) =>
      val mess = MessagesNLP.NLPReady(username)
      val d = Pickle.intoBytes[MessagesNLP.Message](mess)
      send(d)

    case result: MessagesNLP.Connected =>
      nlp ! WorkMessages.AskStatus(self)
      val d = Pickle.intoBytes[MessagesNLP.Message](result)
      send(d)

    case MessagesNLP.Disconnected(user, channel, list) =>
      log.info(s"User $user disconnected from channel $channel")

    case  result @ MessagesNLP.DocumentAnnotations(document, mens) =>
      val d = Pickle.intoBytes[MessagesNLP.Message](result)
      send(d)


  }

  protected def onOtherMessage: Receive = {

    case ActorPublisherMessage.Request(n) => deliverBuf()

    case other => log.error(s"Unknown other message: $other")
  }


  override def receive: Receive =  onTextMessage.orElse(onBinaryMessage).orElse(onServerMessage).orElse(onOtherMessage)

  context.system.scheduler.schedule(10 seconds, 20 seconds){ //TODO: rewrite to timeout configs
    println(s"keep $username alive")
    val d = Pickle.intoBytes[MessagesNLP.Message](KeepAlive(username))
    send(d)
  }
}
