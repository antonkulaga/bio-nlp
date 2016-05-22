package org.denigma.nlp.communication

import java.io.{InputStream, File => JFile}
import java.nio.ByteBuffer
import java.time._
import java.util.UUID

import akka.Done
import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.http.scaladsl.model.ws.{BinaryMessage, TextMessage}
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.actor.{ActorPublisher, ActorPublisherMessage}
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString
import boopickle.DefaultBasic._
import org.denigma.nlp.MessagesNLP
import org.denigma.nlp.communication.SocketMessages.OutgoingMessage

import scala.annotation.tailrec
import scala.collection.immutable.SortedSet
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class UserActor(val username: String, servers: ActorRef) extends Messenger
{

  def readResource(path: String): Iterator[String] = {
    val stream: InputStream = getClass.getResourceAsStream(path)
    scala.io.Source.fromInputStream( stream ).getLines
  }


  protected def onTextMessage: Receive = {
    case SocketMessages.IncomingMessage(channel, uname, TextMessage.Strict(text), time) =>
  }

  protected def otherKappaMessages: Receive  = {
    case other => log.error(s"unexpected $other")
  }


  protected def onBinaryMessage: Receive = {
    case SocketMessages.IncomingMessage(channel, uname, message: BinaryMessage.Strict, time) =>
      val mes = Unpickle[MessagesNLP.Message].fromBytes(message.data.toByteBuffer)
      val fun = (otherKappaMessages)
      fun(mes)
    //log.error(s"something binary received on $channel by $username")
  }

  protected def onServerMessage: Receive = {

    case s : MessagesNLP.ServerErrors=>
      val d = Pickle.intoBytes[MessagesNLP.Message](s)
      send(d)

    case result: MessagesNLP.Connected =>
      val d = Pickle.intoBytes[MessagesNLP.Message](result)
      send(d)

    case MessagesNLP.Disconnected(user, channel, list) =>
      log.info(s"User $user disconnected from channel $channel")

  }

  protected def onOtherMessage: Receive = {

    case ActorPublisherMessage.Request(n) => deliverBuf()

    case other => log.error(s"Unknown other message: $other")
  }


  override def receive: Receive =  onTextMessage.orElse(onBinaryMessage).orElse(onServerMessage).orElse(onOtherMessage)


}
