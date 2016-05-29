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
class EntityAttributeType(val `type`: String, vals: Map[String, String]) extends js.Object{

  import scala.scalajs.js.JSConverters._
  val values: Dictionary[String] = vals.toJSDictionary

}

@ScalaJSDefined
class ColData(types: List[EntityType], relationTypes: List[RelationType] = Nil, attributes: List[EntityAttributeType] = Nil, events: List[EventType] = Nil) extends js.Object
{

  val entity_types: js.Array[EntityType] =  types.toJSArray

  val entity_attribute_types = attributes.toJSArray

  val relation_types: Array[RelationType] = relationTypes.toJSArray

  val event_types = events.toJSArray

}

@ScalaJSDefined
class EntityType(val `type`: String, lbs: Seq[String], val bgColor: String, val borderColor: String) extends js.Object
{
  val labels: js.Array[String]=  lbs.toJSArray
}
