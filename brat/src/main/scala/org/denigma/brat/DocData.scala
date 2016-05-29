package org.denigma.brat

import scala.scalajs.js
import scala.scalajs.js.{Array, Dictionary}
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.ScalaJSDefined


trait BratObject{
  def id: String
}

case class Entity(id: String, tp: String, pairs: List[(Int, Int)]) extends BratObject
{
  //[${ID}, ${TYPE}, [[${START}, ${END}]]
  lazy val toJSArray = {
    val chunks = pairs.map{case (from, to)=> js.Array(from, to)}
    js.Array(id, tp, chunks.toJSArray)
  }
}

case class DocAttribute(id: String, tp: String, target: String)  extends BratObject
{

  // Format: [${ID}, ${TYPE}, ${TARGET}]
  // ['A1', 'Notorious', 'T4']
  lazy val toJSArray = js.Array(id, tp, target)

}
/*
*  // Format: [${ID}, ${TYPE}, [[${ARGNAME}, ${TARGET}], [${ARGNAME}, ${TARGET}]]]
    ['R1', 'Anaphora', [['Anaphor', 'T2'], ['Entity', 'T1']]]
* */
case class Relation(id: String, tp: String, arg1: String, target1: String, arg2: String, target2: String)  extends BratObject
{
  lazy val toJSArray: Array[Object] = js.Array(id, tp, js.Array(js.Array(arg1, target1),js.Array(arg2, target2)))
}

case class BratEvent(id: String, trigger: String, args: List[(String, String)]) extends BratObject
{

  val arguments: Array[Array[String]] = args.map{case (key, value)=> js.Array(key, value)}.toJSArray

  // Format: [${ID}, ${TRIGGER}, [[${ARGTYPE}, ${ARGID}], ...]]
  //['E1', 'T5', [['Perpetrator', 'T3'], ['Victim', 'T4']]],
  lazy val toJSArray = js.Array(id, trigger, arguments)
}

object DocData {
  def apply(text: String,
            entities: List[Entity],
            attribs: List[DocAttribute] = Nil,
            rels: List[Relation] = Nil,
            trigs: List[Entity] = Nil,
            events: List[BratEvent] = Nil,
            comms : List[js.Array[String]] = Nil
           ) = {
    new DocData(text, entities, attribs, rels, events, trigs)
  }
}

@ScalaJSDefined
class DocData(val text: String,
              elements: List[Entity],
              attribs: List[DocAttribute] = Nil,
              rels: List[Relation] = Nil,
              evs: List[BratEvent] = Nil,
              trigs: List[Entity] = Nil,
              comms : List[js.Array[String]] = Nil
             ) extends js.Object
{
  val entities: Array[Array[Object]] = elements.map(p=>p.toJSArray).toJSArray

  val attributes = attribs.map(_.toJSArray).toJSArray

  val relations = rels.map(r=>r.toJSArray).toJSArray

  val events: Array[Array[Object]] = evs.map(ev=>ev.toJSArray).toJSArray

  val triggers: Array[Array[Object]] = trigs.map(p=>p.toJSArray).toJSArray

  val collection = null //some weird thing

  val comments = comms.toJSArray

}
