package org.denigma.nlp.messages

import boopickle.CompositePickler

object MessagesNLP {

  object Message {

    import boopickle.DefaultBasic._
    implicit val simpleMessagePickler: CompositePickler[Message] = compositePickler[Message]
        .addConcreteType[ServerErrors]
        .addConcreteType[Connected]
        .addConcreteType[Disconnected]
        .addConcreteType[Annotate]
        .addConcreteType[NLPReady]
        .addConcreteType[DocumentAnnotations]

  }

  trait Message


  object DocumentAnnotations {
    import boopickle.DefaultBasic._

    implicit val pickler: Pickler[DocumentAnnotations] = PicklerGenerator.generatePickler[DocumentAnnotations]

    lazy val empty: DocumentAnnotations = DocumentAnnotations(Annotations.Document.empty, Nil)
  }


  case class DocumentAnnotations(document: Annotations.Document, mentions: List[Annotations.Mention]) extends Message

  object ServerErrors{
    import boopickle.DefaultBasic._

    implicit val pickler: Pickler[ServerErrors] = PicklerGenerator.generatePickler[ServerErrors]
  }

  case class ServerErrors(errors: List[String]) extends Message

  object Connected{
    import boopickle.DefaultBasic._

    implicit val pickler: Pickler[Connected] = PicklerGenerator.generatePickler[Connected]
  }


  case class Connected(username: String, channel: String, users: List[String]) extends Message

  object Disconnected{
    import boopickle.DefaultBasic._

    implicit val pickler: Pickler[Disconnected] = PicklerGenerator.generatePickler[Disconnected]
  }

  case class Disconnected(username: String, channel: String, users: List[String]) extends Message


  object Annotate{
    import boopickle.DefaultBasic._

    implicit val pickler: Pickler[Annotate] = PicklerGenerator.generatePickler[Annotate]
  }


  case class Annotate(text: String) extends Message

  object NLPReady{
    import boopickle.DefaultBasic._

    implicit val pickler: Pickler[NLPReady] = PicklerGenerator.generatePickler[NLPReady]
  }

  case class NLPReady(username: String) extends Message

  case object Empty extends Message {
    import boopickle.DefaultBasic._

    implicit val pickler: Pickler[this.type] = PicklerGenerator.generatePickler[this.type]
  }
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
