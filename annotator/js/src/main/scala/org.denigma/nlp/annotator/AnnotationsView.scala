package org.denigma.nlp.annotator

import org.denigma.binding.binders.Events
import org.denigma.binding.views.{BindableView, ItemsMapView, ItemsSeqView, UpdatableView}
import org.denigma.controls.code.CodeBinder
import org.denigma.nlp.messages.Annotations
import org.denigma.nlp.messages.Annotations.{BioMention, Grounding, Mention}
import org.denigma.nlp.messages.MessagesNLP.DocumentAnnotations
import org.scalajs.dom
import org.scalajs.dom.{Element, MouseEvent}
import org.scalajs.dom.raw.{SVGElement, SVGRectElement}
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._
import org.scalajs.dom.svg.Rect

class AnnotationsView(val elem: Element, val items: Rx[Map[Annotations.Mention, (String, SVGElement)]]) extends ItemsMapView {
  type Item = Annotations.Mention
  type ItemView = MentionView

  type Value = (String, SVGElement)

  override def newItemView(item: Item): MentionView = constructItemView(item){
    case (el, _)=>
      new MentionView(el, item).withBinder(v=>new CodeBinder(v))
  }
}

class MentionView(val elem: Element, mention: Annotations.Mention) extends BindableView with UpdatableView[(String, SVGElement)]
{
  val code = Var(str(mention))
  val highlighted = Var(false)

  //val value: (String, SVGElement)

  def str(men: Annotations.Mention) = mention match {
    case mention: Annotations.CorefEventMention =>
      "labels: "+mention.labels+"<br>"+
      "foundby: "+mention.foundBy+"<br>"+
      "ground:"+mention.bestPick.getOrElse("")+"<br>"+
      "context:"+mention.context+"<br>"+
      "arguments: "+mention.arguments+"<br>"+
      "modifications: "+mention.modifications+"<br>"+
      "triggered by: "+mention.trigger+"<br>"+
      "intervals: "+mention.tokenInterval+"<br>"

    case mention :Annotations.BioMention =>
      "labels: "+mention.labels+"<br>"+
      "foundby: "+mention.foundBy+"<br>"+
      "ground:"+mention.bestPick.getOrElse("")+"<br>"+
      "context:"+mention.context+"<br>"+
      "arguments: "+mention.arguments+"<br>"+
      "modifications: "+mention.modifications+"<br>"+
      "intervals: "+mention.tokenInterval+"<br>"

    case mention =>
      "labels: "+mention.labels+"<br>"+
      "foundby: "+mention.foundBy+"<br>"+
      "arguments: "+mention.arguments+"<br>"+
      "intervals: "+mention.tokenInterval+"<br>"
  }

  protected def miriamClick(event: MouseEvent): Unit = mention match {
    case bio: Grounding =>
      bio.grounding match {
        case Some(res) =>
          dom.window.open(res.entry.miriam, "_blank", replace = false)
        case None => println("no resolution")
      }


    case other =>
      //not yet decided
      //dom.window.open(mention.)
  }

  protected def onMouseLeave(event: MouseEvent): Unit = {
    highlighted() = false
  }

  protected def onMouseEnter(event: MouseEvent): Unit = {
    highlighted() = true
  }


  override def update(value: (String, SVGElement)): MentionView.this.type ={
    //not implemented
    val (id, svg) = value
    svg.addEventListener(Events.mouseover, onMouseEnter _)
    svg.addEventListener(Events.mouseout, onMouseLeave _)
    mention match {
      case gr: Grounding if gr.grounding.isDefined =>
       svg match {
         case st: SVGRectElement =>
           st.style.cursor = "pointer"
         case _=>
       }
        svg.addEventListener(Events.click, miriamClick _)
      case _ =>
    }
    this
  }
}