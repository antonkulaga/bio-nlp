package org.denigma.nlp.brat

import scala.scalajs.js
import scala.scalajs.js.Array
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}
import scalajs.js.JSConverters._

@JSName("Util")
@js.native
object BratUtil  extends js.Object {

  def embed(id: String = "annotation", collData: ColData, docData: DocData,  webFontURLs: js.Array[String]): Unit = js.native
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

@ScalaJSDefined
class RelationType(val `type`: String, lbs: List[String], val dashArray: String = "3,3", val color: String = "purple", roles: List[RelationRole]) extends js.Object {

  val labels = lbs.toJSArray

  val args = roles.toJSArray

}
@ScalaJSDefined
class LabeledType(val `type`: String, lbs: List[String], val color: String = "") extends js.Object {

  val labels = lbs.toJSArray
}


@ScalaJSDefined
class EventType(val `type`: String, lbs: List[String], val bgColor: String = "lightgreen", borderColor: String = "darken", types: List[LabeledType]) extends js.Object {
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

@ScalaJSDefined
class ColData(types: List[EntityType], relationTypes: List[RelationType] = Nil, attributes: List[EntityAttributeType] = Nil, events: List[EventType]) extends js.Object
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
class EntityAttributeType(val `type`: String, valueStr: String) extends js.Object{

  val values = scalajs.js.JSON.parse(valueStr)

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


}
