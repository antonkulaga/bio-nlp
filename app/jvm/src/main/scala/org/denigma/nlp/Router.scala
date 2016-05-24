package org.denigma.nlp

import java.io.{File => JFile}

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.extensions.security.LoginInfo
import akka.http.extensions.stubs.{Registration, _}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import better.files.File
import com.typesafe.config.Config
import org.denigma.nlp.communication.WebSocketManager
import org.denigma.nlp.extractions.NLPActor
import org.denigma.nlp.pages.{Head, Pages, WebSockets}

class Router(files: File)(implicit fm: Materializer, system: ActorSystem) extends Directives {

  implicit def ctx = system.dispatcher

  lazy val config: Config = system.settings.config

  val extractor: ActorRef = system.actorOf(Props(classOf[NLPActor], config)/*.withDispatcher("reach-dispatcher")*/) //dedicated thread per NLP

  val sessionController: SessionController = new InMemorySessionController

  val loginController: InMemoryLoginController = new InMemoryLoginController()

  loginController.addUser(LoginInfo("admin", "test2test", "test@email"))

  val transport = new WebSocketManager(system, extractor = extractor)

  def loadFiles: Route = pathPrefix("files" ~ Slash) {
    getFromDirectory(files.path.toString)
  }

  def routes = new Head().routes ~ loadFiles ~
    new Registration(
      loginController.loginByName,
      loginController.loginByEmail,
      loginController.register,
      sessionController.userByToken,
      sessionController.makeToken
    )
      .routes ~
    new Pages().routes ~ new WebSockets(
    //loginController.loginByName,
    //loginController.loginByEmail,
    transport.openChannel).routes
}
