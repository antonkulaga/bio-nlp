package org.denigma.brat

import scala.scalajs.js
import scala.scalajs.js.{Array, Dictionary}
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.ScalaJSDefined

object  RelationRole
{
  def apply(role: String, ts: List[String]): RelationRole = new RelationRole(role, ts)
}

@ScalaJSDefined
class RelationRole(val role: String, ts: List[String]) extends js.Object{
  val targets = ts.toJSArray
}

/*
collData['event_types'] = [ {
    type   : 'Assassination',
    labels : ['Assassination', 'Assas'],
    bgColor: 'lightgreen',
    borderColor: 'darken',
    /* Unlike relations, events originate from a span of text and can take
        several arguments */
    arcs   : [
        {type: 'Victim', labels: ['Victim','Vict'] },
        // Just like the event itself, its arguments can be styled
        {type: 'Perpetrator', labels: ['Perpetrator','Perp'], color: 'green' }
    ]
} ];
 */

object RelationType
{
  def apply(`type`: String, lbs: List[String], dashArray: String = "3,3", color: String = "purple", roles: List[RelationRole] = Nil) =
    new RelationType(`type`, lbs, dashArray, color, roles)
}

@ScalaJSDefined
class RelationType(val `type`: String, lbs: List[String], val dashArray: String = "3,3", val color: String = "purple", roles: List[RelationRole]) extends js.Object {

  val labels = lbs.toJSArray

  val args = roles.toJSArray

}
object LabeledType
{
  def apply(`type`: String, lbs: List[String], color: String = ""): LabeledType = {
    new LabeledType(`type`, lbs, color)
  }
}

@ScalaJSDefined
class LabeledType(val `type`: String, lbs: List[String], val color: String = "") extends js.Object {

  val labels = lbs.toJSArray
}
object EventType {
  def apply(`type`: String, lbs: List[String], types: List[LabeledType], bgColor: String = "lightgreen", borderColor: String = "darken") = {
    new EventType(`type`, lbs, types, bgColor, borderColor)
  }
}

@ScalaJSDefined
class EventType(val `type`: String, lbs: List[String], types: List[LabeledType], val bgColor: String = "lightgreen", borderColor: String = "darken") extends js.Object {
  val labels: Array[String] = lbs.toJSArray
  val arcs = types.toJSArray
/*
 arcs   : [
        {type: 'Victim', labels: ['Victim','Vict'] },
        // Just like the event itself, its arguments can be styled
        {type: 'Perpetrator', labels: ['Perpetrator','Perp'], color: 'green' }
    ]
 */
}

object ColData {
  def apply(types: List[EntityType], relationTypes: List[RelationType] = Nil, attributes: List[EntityAttributeType] = Nil, events: List[EventType]) = {
    new ColData(types, relationTypes, attributes, events)
  }
}

@ScalaJSDefined
class ColData(types: List[EntityType], relationTypes: List[RelationType] = Nil, attributes: List[EntityAttributeType] = Nil, events: List[EventType] = Nil) extends js.Object
{

  val entity_types: js.Array[EntityType] =  types.toJSArray

  val entity_attribute_types = attributes.toJSArray

  val relation_types: Array[RelationType] = relationTypes.toJSArray

  val event_types = events.toJSArray

  println(js.JSON.stringify(event_types))

}

@ScalaJSDefined
class EntityType(val `type`: String, lbs: Seq[String], val bgColor: String, val borderColor: String) extends js.Object
{
  val labels: js.Array[String]=  lbs.toJSArray
}

case class Entity(id: String, tp: String, pairs: List[(Int, Int)])
{
  //[${ID}, ${TYPE}, [[${START}, ${END}]]
  lazy val toJSArray = {
    val chunks = pairs.map{case (from, to)=> js.Array(from, to)}
    js.Array(id, tp, chunks.toJSArray)
  }
}

case class DocAttribute(id: String, tp: String, target: String) {

  // Format: [${ID}, ${TYPE}, ${TARGET}]
  // ['A1', 'Notorious', 'T4']
  lazy val toJSArray = js.Array(id, tp, target)

}


@ScalaJSDefined
class EntityAttributeType(val `type`: String, vals: Map[String, String]) extends js.Object{

  import scala.scalajs.js.JSConverters._
  val values: Dictionary[String] = vals.toJSDictionary

}
/*
*  // Format: [${ID}, ${TYPE}, [[${ARGNAME}, ${TARGET}], [${ARGNAME}, ${TARGET}]]]
    ['R1', 'Anaphora', [['Anaphor', 'T2'], ['Entity', 'T1']]]
* */
case class Relation(id: String, tp: String, arg1: String, target1: String, arg2: String, target2: String) {
  lazy val toJSArray: Array[Object] = js.Array(id, tp, js.Array(js.Array(arg1, target1),js.Array(arg2, target2)))
}

case class BratEvent(id: String, trigger: String, args: List[(String, String)]) {

  val arguments: Array[Array[String]] = args.map{case (key, value)=> js.Array(key, value)}.toJSArray

  // Format: [${ID}, ${TRIGGER}, [[${ARGTYPE}, ${ARGID}], ...]]
  //['E1', 'T5', [['Perpetrator', 'T3'], ['Victim', 'T4']]],
  lazy val toJSArray = js.Array(id, trigger, arguments)
}

object DocData {
  def apply(text: String, entities: List[Entity], attribs: List[DocAttribute] = Nil, rels: List[Relation] = Nil, events: List[BratEvent] = Nil, trigs: List[Entity] = Nil) = {
    new DocData(text, entities, attribs, rels, events, trigs)
  }
}

@ScalaJSDefined
class DocData(val text: String,
              elements: List[Entity],
              attribs: List[DocAttribute] = Nil,
              rels: List[Relation] = Nil,
              evs: List[BratEvent] = Nil,
              trigs: List[Entity] = Nil
             ) extends js.Object
{
  val entities: Array[Array[Object]] = elements.map(p=>p.toJSArray).toJSArray

  val attributes = attribs.map(_.toJSArray).toJSArray

  val relations = rels.map(r=>r.toJSArray).toJSArray

  val events: Array[Array[Object]] = evs.map(ev=>ev.toJSArray).toJSArray
  println(js.JSON.stringify(events))

  val triggers: Array[Array[Object]] = trigs.map(p=>p.toJSArray).toJSArray

  val collection = null //some weird thing


}
