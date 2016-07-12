package org.denigma.nlp.annotator

import org.denigma.binding.binders.Events
import org.denigma.binding.views.{BindableView, ItemsMapView, UpdatableView}
import org.denigma.controls.code.CodeBinder
import org.denigma.nlp.messages.Annotations
import org.denigma.nlp.messages.Annotations.Grounding
import org.scalajs.dom
import org.scalajs.dom.raw.{SVGElement, SVGRectElement}
import org.scalajs.dom.{Element, MouseEvent}
import rx._
import scalatags.Text.all._

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
  val other = Var(strOther(mention))
  val highlighted = Var(false)

  val labels = Var(mention.labels.foldLeft(""){
    case (acc, el) => el.trim
    case (acc, el) => ", " + el.trim
  })

  val foundBy = Var(mention.foundBy)
  val arguments = Var(mention.arguments)
  val interval = Var(mention.tokenInterval.start + " to " + mention.tokenInterval.end)


  //val value: (String, SVGElement)

  def strOther(men: Annotations.Mention) = mention match {
    case mention: Annotations.CorefEventMention =>
      val pick: String = mention.bestPick.map(p=>p.toString).getOrElse("")
      val foot1 = tr(
        th("context"),
        td(mention.context.toString()),
        th("ground"),
        td(pick)
      )
      val foot2 = tr(
        th("modifications"),
        td(mention.modifications.toString()),
        th("triggered by"),
        td(mention.trigger.toString)
      )
      foot1.render +"\n"+foot2.render

    case mention :Annotations.BioMention =>
      val pick: String = mention.bestPick.map(p=>p.toString).getOrElse("")
      val foot1 = tr(
        th("context"),
        td(mention.context.toString()),
        th("modifications"),
        td(mention.modifications.toString())
      )
      val foot2 = tr(
        th("ground"),
        td(pick, colspan := 3)
      )
      foot1.render +"\n"+foot2.render

    case _ => ""

  }

  /*
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
  */

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