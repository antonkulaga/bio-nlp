package org.denigma.nlp.communication

import java.nio.ByteBuffer

import boopickle.DefaultBasic._
import org.denigma.nlp.messages.MessagesNLP
import org.denigma.binding.extensions._
import org.denigma.controls.sockets.{WebSocketTransport1, _}
import org.scalajs.dom
import org.scalajs.dom.raw.WebSocket
import rx.Ctx.Owner.Unsafe.Unsafe
import rx.Var



case class WebSocketNLPTransport(channel: String, username: String) extends WebSocketTransport1
{

  type Input = MessagesNLP.Message


  override val connected = Var(false)
  lazy val NLPready = Var(false)
  /*
  lazy val ready: Rx[Boolean] = Rx {
    connected() && NLPready()
  }
  */

  input.triggerLater{
    onInput(input.now)
  }

  def collect[Output](partialFunction: PartialFunction[Input, Output])(until: PartialFunction[Input, Boolean]) = {
    new MessageCollecter[Input, Output](input)(partialFunction)(until).future
  }


  protected def onInput(inp: Input) = inp match {
    case MessagesNLP.Connected(uname, ch, list) if uname==username /*&& ch == channel*/ =>
      println(s"connection of user $username to $channel established")
      connected() = true

    case MessagesNLP.Disconnected(uname, ch, list) if uname==username /* && ch == channel */ =>
      println(s"user $username diconnected from $channel")
      connected() = false

    case MessagesNLP.NLPReady(uname) if uname==username =>
      NLPready() = true

    case _=> //do nothing
  }

  override def send(message: Output): Unit = if(NLPready.now) { //TODO/ FIX THIS SUPERBUGGY thing
    val mes = bytes2message(pickle(message))
    send(mes)
  } else {
    NLPready.triggerOnce{
      case true =>
        send(message)
      case false =>
    }
  }


  override protected def closeHandler() = {
    println("websocket closed")
    connected() = false
    opened() = false
  }

  override def getWebSocketUri(username: String): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/channel/$channel?username=$username"
  }

  def open(): Unit = {
    urlOpt() = Option(getWebSocketUri(username))
  }

  override def initWebSocket(url: String): WebSocket = WebSocketStorage(url)

  override def emptyInput: MessagesNLP.Message = MessagesNLP.Empty

  override protected def pickle(message: Output): ByteBuffer = {
    Pickle.intoBytes(message)
  }

  override protected def unpickle(bytes: ByteBuffer): MessagesNLP.Message= {
    Unpickle[Input].fromBytes(bytes)
  }
}