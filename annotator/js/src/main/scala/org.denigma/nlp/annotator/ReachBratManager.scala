package org.denigma.nlp.annotator

import org.denigma.brat.extensions.BratManager
import org.denigma.nlp.messages.MessagesNLP
import org.denigma.nlp.messages.Annotations
import org.denigma.nlp.messages.Annotations.Mention
import org.scalajs.dom.ext._
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.SVGElement
import rx._
import rx.Ctx.Owner.Unsafe.Unsafe
import org.denigma.binding.extensions._
import org.scalajs.dom
import rx.Rx.Dynamic

import scala.annotation.tailrec
import scala.scalajs.js
import scalajs.js.JSConverters._
import org.denigma.brat.extensions._

class ReachBratManager(container: String, webFontURLs: List[String], val annotations: Var[MessagesNLP.DocumentAnnotations]) extends BratManager(container, webFontURLs){

  val reachBratModel = ReachBratModel()


  disp.onDoneRendering{
    () =>
    val svg = visualizer.svg._svg
    if(svg!=null)
      {
        named() = extractLinks(svg).toMap
        //named.now.foreach(n=>pprint.pprintln(n))
      } else dom.console.error("SVG IS NULL")
  }

  val mentions: Dynamic[Map[Mention, String]] = annotations.map(ans=>ans.mentionsWithIDs)

  val named: Var[Map[String, SVGElement]] =  Var(Map.empty[String, SVGElement])

  val elements: Rx[Map[Mention, (String, SVGElement)]] = named.map{ case mp =>
    mentions.now.collect{
      case (men, id) if mp.contains(id) => men -> (id, mp(id))
    }
  }

  mentions.onChange{
     case mens =>
      //val mentionsWithIDs: Map[String, Mention] = mens.map{ case (m, id)=> (id, m)}
      val docData = reachBratModel.docData(annotations.now.document, mens)
      super.update(reachBratModel.colData, docData)
  }


  protected def hasAttribute(el: SVGElement, att: String) = el != null && /*!js.isUndefined(el.dyn.hasAttribute) &&*/ el.hasAttribute(att)

  protected def extractLinks(element: SVGElement): List[(String, SVGElement)] = element match {
    case el if hasAttribute(el, dataID) =>
      el.getAttribute(dataID) -> el::Nil

    case other =>
      other.children.foldLeft(List.empty[(String, SVGElement)]){
        case (acc, el: SVGElement) =>
          extractLinks(el) match {
            case e::Nil => e::acc
            case els => els ++ acc
          }
        case (acc, _) => dom.console.error("something else"+other)
          acc
      }
  }


}
