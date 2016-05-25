package org.denigma.nlp.messages.Annotations

import boopickle._

object Modification {
  import boopickle.DefaultBasic._
  implicit val mentionPickler: CompositePickler[Modification] = compositePickler[Modification]
    .addConcreteType[PTM]
    .addConcreteType[Mutant]
    .addConcreteType[EventSite]
    .addConcreteType[Negation]
    .addConcreteType[Hypothesis]

}

trait Modification {
  // modifications should at least have a label that explains
  // what kind of modification they are
  def label: String

  def matches(query: String): Boolean = this.label == query
}

object PTM {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[PTM] = PicklerGenerator.generatePickler[PTM]
}

case class PTM(
                label: String,
                evidence: Option[Mention] = None,
                site: Option[Mention] = None
              ) extends Modification {
  /*
  override def toString: String = {
    val b = new StringBuilder()
    b.append(label)
    if (site.isDefined)
      b.append(" @ " + site.get.text)
    b.toString()
  }
  */
}

object Mutant {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[Mutant] = PicklerGenerator.generatePickler[Mutant]
}

case class Mutant(evidence: Mention, foundBy: String) extends Modification{
  val label = evidence.label
  //val text = evidence.text

  //def isGeneric: Boolean = evidence.toCorefMention.isGeneric

  override def hashCode: Int = evidence.hashCode() * 42 + label.hashCode()
}

object EventSite {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[EventSite] = PicklerGenerator.generatePickler[EventSite]
}

case class EventSite(site: Mention) extends Modification {
  val label = "EventSite"
}

object Negation {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[Negation] = PicklerGenerator.generatePickler[Negation]
}

case class Negation(evidence: Mention) extends Modification {
  val label = "Negation"
}

object Hypothesis {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[Hypothesis] = PicklerGenerator.generatePickler[Hypothesis]
}

case class Hypothesis(evidence: Mention) extends Modification{
  val label = "Hypothesis"
}
