package org.denigma.nlp

import java.io.{File => JFile}
import java.nio.ByteBuffer

import akka.http.scaladsl.model.ws.BinaryMessage.Strict
import akka.http.scaladsl.model.ws.{BinaryMessage, Message}
import akka.http.scaladsl.testkit.WSProbe
import akka.stream.testkit.TestSubscriber
import akka.util.ByteString
import boopickle.DefaultBasic._
import org.denigma.nlp.messages.MessagesNLP

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

abstract class BasicWebSocketSuite extends BasicKappaSuite  {

  lazy val (host, port) = (config.getString("app.host"), config.getInt("app.port"))

  def pack(buffer:  ByteBuffer): Strict = BinaryMessage(ByteString(buffer))

  def unpack(mess: Message): Try[MessagesNLP.Message] = mess match {
    case BinaryMessage.Strict(bytes)=> Unpickle[MessagesNLP.Message].tryFromBytes(bytes.toByteBuffer)
    case other => Failure(new Exception("not a binary message"))
  }

  def checkMessage[T](wsClient: WSProbe, message: ByteBuffer)(partial: PartialFunction[MessagesNLP.Message, T]): T = {
    wsClient.sendMessage(pack(message))
    wsClient.inProbe.request(1).expectNextPF {
      case BinaryMessage.Strict(bytes) if {
        Unpickle[MessagesNLP.Message].fromBytes(bytes.asByteBuffer) match {
          case l =>
            if(partial.isDefinedAt(l)) true else {
              println("checkProjects failed with message "+l)
              false
            }
        }
      } =>
        val value = Unpickle[MessagesNLP.Message].fromBytes(bytes.asByteBuffer)
        partial(value)
    }
  }
  implicit protected def toMessagePartial[T](messagePartial: PartialFunction[MessagesNLP.Message, T]): PartialFunction[Message, T] = new PartialFunction[Message, T] {
    def apply(message: Message) = messagePartial(unpack(message).asInstanceOf[Success[MessagesNLP.Message]].get)
    def isDefinedAt(message: Message) = unpack(message) match {
      case Success(mess)=> messagePartial.isDefinedAt(mess)
      case _ => false
    }
  }

  @tailrec final def probeLoop[T](probe: TestSubscriber.Probe[Message], message: Message, fun: PartialFunction[Message, T]): T = if(fun.isDefinedAt(message)) {
    fun(message)
  } else {
    probeLoop(probe, probe.requestNext(), fun)
  }

  @tailrec final def probeCollectUntilLoop[T](probe: TestSubscriber.Probe[Message],
                                         message: Message,
                                         collected: List[T],
                                         collect: PartialFunction[Message, T],
                                         until: PartialFunction[Message, Boolean]
                                        ): List[T] =
  if(until.isDefinedAt(message) && until(message)) {
    collected.reverse
  } else {
    val acc = if(collect.isDefinedAt(message)) collect(message)::collected else collected
    probeCollectUntilLoop(probe, probe.requestNext(), acc, collect, until)
    //probeCollectLoop(probe, probe.requestNext(), fun)
  }

  protected def waitPartialKappaMessage[T](probe: TestSubscriber.Probe[Message], timeout: FiniteDuration = 5000 millis)(partial: PartialFunction[MessagesNLP.Message, T]) =
    waitPartialMessage(probe, timeout)(partial)

  protected def waitPartialMessage[T](probe: TestSubscriber.Probe[Message], timeout: FiniteDuration = 5000 millis)(fun: PartialFunction[Message, T]) = {
    val future = Future {
      val result = probeLoop(probe, probe.requestNext(), fun)
      result
    }
    Await.result(future, timeout)
  }

  protected def collectPartialKappaMessage[T](probe: TestSubscriber.Probe[Message], timeout: FiniteDuration = 5000 millis)
                                        (collect: PartialFunction[MessagesNLP.Message, T])
                                        (until: PartialFunction[MessagesNLP.Message, Boolean]): List[T] = {
    val future = Future {
      val result: List[T] = probeCollectUntilLoop(probe, probe.requestNext(), List.empty[T], collect, until)
      result
    }
    Await.result(future, timeout)
  }


  protected def collectPartialMessage[T](probe: TestSubscriber.Probe[Message], timeout: FiniteDuration = 5000 millis)
                                     (collect: PartialFunction[Message, T])(until: PartialFunction[Message, Boolean]): List[T] = {
    val future = Future {
      val result: List[T] = probeCollectUntilLoop(probe, probe.requestNext(), List.empty[T], collect, until)
      result
    }
    Await.result(future, timeout)
  }

  def checkProject[T](wsClient: WSProbe, projectToLoad: KappaProject)(partial: PartialFunction[MessagesNLP.Message, T]): T =
  {
    val bytes = Pickle.intoBytes[MessagesNLP.Message](ProjectRequests.Load(projectToLoad))
    checkMessage[T](wsClient, bytes)(partial)
  }


  def checkConnection(wsClient: WSProbe): Unit = {
    isWebSocketUpgrade shouldEqual true

    wsClient.inProbe.request(1).expectNextPF {
      case BinaryMessage.Strict(bytes) if {
        Unpickle[MessagesNLP.Message].fromBytes(bytes.asByteBuffer) match {
          case c: Connected => true
          case _ => false
        }
      } =>
    }
  }

}
