package org.denigma.nlp

import java.io.{File => JFile}
import java.nio.ByteBuffer

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.testkit.WSProbe
import better.files.File
import boopickle.DefaultBasic._
import net.ceedubs.ficus.Ficus._
import org.denigma.nlp.communication.WebSocketManager
import org.denigma.nlp.extractions.ExtractorWorker
import org.denigma.nlp.pages.WebSockets

import scala.List
import scala.collection.immutable._
import scala.concurrent.duration._

class WebSocketAnnotationSuite extends BasicWebSocketSuite {

  val extractor: ActorRef = system.actorOf(Props(classOf[ExtractorWorker], config))

  val transport = new WebSocketManager(system, extractor)

  val routes = new WebSockets(transport.openChannel).routes

  "Anotations via websockets" should {

    "load document annotation for the sentence" in {
      val wsClient = WSProbe()
      WS("/channel/notebook?username=tester3", wsClient.flow) ~> routes ~>
        check {
          checkConnection(wsClient)
          println("it works!")
        }
      wsClient.sendCompletion()
      //wsClient.expectCompletion()
    }

  }

}

