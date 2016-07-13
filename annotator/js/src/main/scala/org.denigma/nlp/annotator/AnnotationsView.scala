package org.denigma.nlp.annotator

import org.denigma.binding.binders.Events
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.{BasicView, BindableView, ItemsMapView, UpdatableView}
import org.denigma.controls.code.CodeBinder
import org.denigma.nlp.messages.Annotations
import org.denigma.nlp.messages.Annotations.{KBResolution, Grounding}
import org.scalajs.dom
import org.scalajs.dom.raw.{SVGElement, SVGRectElement}
import org.scalajs.dom.{Element, MouseEvent}
import rx._
import scalatags.Text.all._


class AnnotationsView(val elem: Element, val items: Rx[Map[Annotations.Mention, (String, SVGElement)]]) extends ItemsMapView {
  type Item = Annotations.Mention
  type ItemView = MentionView

  type Value = (String, SVGElement)

  protected def resolve(mention: Annotations.Mention): Option[Value]= items.now.get(mention)

  override def newItemView(item: Item): MentionView = constructItemView(item){
    case (el, _)=>
      resolve(item).foreach{case (str, svg)=> el.id = str+"_item"}
      new MentionView(el, item, resolve).withBinder(v=>new CodeBinder(v))
  }
}

class MentionView(val elem: Element, mention: Annotations.Mention, //TODO: fix this bad code!
                  resolve: Annotations.Mention => Option[(String, SVGElement)]) extends BindableView with UpdatableView[(String, SVGElement)]
{
  val other = Var(strOther(mention))
  val highlighted = Var(false)
  val arguments = Var(mention.arguments.keys.reduce(_+"; "+_))

  val labels = Var(mention.labels.foldLeft(""){
    case ("", el) => el.trim
    case (acc, el) => ", " + el.trim
  })

  val foundBy = Var(mention.foundBy)
  val interval = Var(mention.tokenInterval.start + " to " + mention.tokenInterval.end)

  protected def resolution2Row(kb: KBResolution) = {
    val metaText: String = kb.metaInfo.foldLeft(""){ case (acc, (key, value))=> acc + key +"="+value+ " " }
    val entryText = kb.entry.toString
    tr(
      td(entryText),
      td(metaText)
    )
  }
  protected def resolution2Table(kbs: List[KBResolution]) = kbs match {
    case Nil => table()
    case lst =>
      val header =  tr(
        th("KB Entry"),
        th("Meta Info")
      )
      val res = header::lst.map(resolution2Row)

      table(
        res:_*
      )
  }


  //val value: (String, SVGElement)

  def strOther(men: Annotations.Mention) = mention match {
    case mention: Annotations.CorefEventMention =>
      val pick= resolution2Table(mention.bestPick.map(List(_)).getOrElse(Nil))
      val foot1 = tr(
        th("context"),
        td(mention.context.toString()),
        th("ground"),
        td(pick)
      )
      val trigBy = resolve(mention.trigger) match {
        case Some((i, svg))=>
          println("OUTER ELEMENT = "+svg.outerHTML)
          td(a(href := "#"+i))
        case None=>
          td(mention.trigger.toString)

      }

      val mods = mention.modifications.foldLeft(""){
        case (acc, m) => acc + m.toString + "<br>"
      }

      val foot2 = tr(
        th("modifications"),
        td(mods),
        th("triggered by"),
        trigBy
      )
      val candidates = tr(
        th("candidates"),
        td(resolution2Table(mention.candidates))
      )
      foot1.render +"\n"+foot2.render+"\n"+candidates.render

    case mention :Annotations.BioMention =>

      val mods = mention.modifications.foldLeft(""){
        case (acc, m) => acc + m.toString + "<br>"
      }
      val foot1 = tr(
        th("context"),
        td(mention.context.toString()),
        th("modifications"),
        td(mods)
      )
      val pick = resolution2Table(mention.bestPick.map(List(_)).getOrElse(Nil))
      val foot2 = tr(
        th("ground"),
        td(pick, colspan := 3)
      )
      val candidates = tr(
        th("candidates"),
        td(resolution2Table(mention.candidates))
      )
      foot1.render +"\n"+foot2.render+"\n"+candidates.render

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
          dom.window.open(res.entry.url, "_blank", replace = false)
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