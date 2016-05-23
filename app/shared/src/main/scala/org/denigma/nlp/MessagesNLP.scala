package org.denigma.nlp

import boopickle.CompositePickler

object MessagesNLP {

  object Message {

    import boopickle.Default._
    implicit val simpleMessagePickler: CompositePickler[Message] = compositePickler[Message]
        .addConcreteType[ServerErrors]
        .addConcreteType[Connected]
        .addConcreteType[Disconnected]
        .addConcreteType[Annotate]

  }

  trait Message

  case class ServerErrors(errors: List[String]) extends Message

  case class Connected(username: String, channel: String, users: List[String]) extends Message

  case class Disconnected(username: String, channel: String, users: List[String]) extends Message

  case class Annotate(text: String) extends Message

  case object Empty extends Message


}
