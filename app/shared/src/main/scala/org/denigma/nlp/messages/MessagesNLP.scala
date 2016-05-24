package org.denigma.nlp.messages

import boopickle.CompositePickler

object MessagesNLP {

  object Message {

    import boopickle.Default._
    implicit val simpleMessagePickler: CompositePickler[Message] = compositePickler[Message]
        .addConcreteType[ServerErrors]
        .addConcreteType[Connected]
        .addConcreteType[Disconnected]
        .addConcreteType[Annotate]
        .addConcreteType[ReachStatus]

  }

  trait Message

  case class ServerErrors(errors: List[String]) extends Message

  case class Connected(username: String, channel: String, users: List[String]) extends Message

  case class Disconnected(username: String, channel: String, users: List[String]) extends Message

  case class Annotate(text: String) extends Message

  case class ReachStatus(ready: Boolean) extends Message

  case object Empty extends Message
/* case class Annotation(displaLabel: String,
                        arguments: Map[String, String],
                        modifications: Set[Modification],
                        context: Option[Map[String, Seq[String]]] = None) extends Message
----------------------------

  import edu.arizona.sista.odin.Mention

  trait Modifications {
    this: Mention =>

    var modifications: Set[Modification] = Set.empty

    def isModified: Boolean = modifications.nonEmpty

    def mutants: Set[Mutant] = modifications.filter(_.isInstanceOf[Mutant]).asInstanceOf[Set[Mutant]]
  }

  sealed trait Modification {
    // modifications should at least have a label that explains
    // what kind of modification they are
    def label: String

    def matches(query: String): Boolean = this.label == query
  }

  case class PTM(
                  label: String,
                  evidence: Option[Mention] = None,
                  site: Option[Mention] = None
                ) extends Modification {
    override def toString: String = {
      val b = new StringBuilder()
      b.append(label)
      if (site.isDefined)
        b.append(" @ " + site.get.text)
      b.toString()
    }
  }

  case class Mutant(evidence: Mention, foundBy: String) extends Modification{
    val label = evidence.label
    val text = evidence.text

    def isGeneric: Boolean = evidence.toCorefMention.isGeneric

    override def hashCode: Int = evidence.hashCode() * 42 + label.hashCode()
  }

  case class EventSite(site: Mention) extends Modification {
    val label = "EventSite"
  }

  case class Negation(evidence: Mention) extends Modification {
    val label = "Negation"
  }

  case class Hypothesis(evidence: Mention) extends Modification{
    val label = "Hypothesis"
  }

*/


}
