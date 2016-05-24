package org.denigma.nlp.communication

import akka.actor.ActorRef

/**
  * Created by antonkulaga on 24/05/16.
  */
object WorkMessages {

  trait Message
  trait ReachStatus extends Message
  case class AskStatus(sender: ActorRef) extends ReachStatus
  case class ReachReady(ready: Boolean = true) extends ReachStatus
}
