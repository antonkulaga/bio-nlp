package org.denigma.nlp.scrollbar

import org.denigma.binding.binders.Events
import org.denigma.binding.extensions.{sq, _}
import org.denigma.binding.views.BindableView
import org.querki.jquery.$
import org.scalajs.dom
import org.scalajs.dom.raw.{Element, PopStateEvent}
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._
import scrollbar._
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

trait ScrollerView extends BindableView{

  def scrollPanel: Element

  lazy val scroller: JQueryScrollbar = initScroller()

  val backClick = Var(Events.createMouseEvent())
  backClick.onChange{
    case ev=>
      scrollHistory.now match {
        case Nil =>
          dom.console.error("back should be invisible when there is not history")
        //do nothing

        case head::Nil =>
          scrollHistory() = Nil
          dom.window.history.back()
          scrollPanel.scrollLeft = head.currentPosition

        case head::tail =>
          scrollHistory() = tail
          dom.window.history.back()
      }
  }

  val forwardClick = Var(Events.createMouseEvent())
  forwardClick.onChange{
    case ev=>
    //dom.window.history.back()
  }
  val scrollHistory: Var[List[ScrollPosition]] = Var(List.empty[ScrollPosition])

  val hasHistory = scrollHistory.map(v=>v.nonEmpty)

  protected def historyState: Option[ScrollPosition] = dom.window.history.state match {
    case some if js.isUndefined(some) => None
    case right if !js.isUndefined(right.dyn.index)=>Some(right.asInstanceOf[ScrollPosition])
    case other => None
  }


  def moveToPlace(tid: String): Unit = sq.byId(tid) match {
    case Some(target) =>
      //val index = historyState.map(v=>v.index).getOrElse(0) + 1
      val stateObject = new ScrollPosition(tid, scrollPanel.scrollLeft)
      dom.window.history.pushState(stateObject, tid, "#"+tid)
      val state = js.Dynamic.literal(
        bubbles = false,
        cancelable = false,
        state = stateObject
      )
      var popStateEvent = new FixedPopStateEvent("popstate", state)
      scrollHistory() = stateObject::scrollHistory.now
      dom.window.dispatchEvent(popStateEvent)
    //println("pop event dispatched")
    case None =>
      dom.console.error("cannot find id "+tid)
  }


  protected def scrollTo(ident: String) = {
    sq.byId(ident) match {
      case Some(element)=>
        //val left = element.offsetLeft
        //scrollPanel.scrollLeft = left
        scroller.scrollTo("#"+ident)
      case None =>
        dom.console.error("cannot scroll to "+ident)
    }
  }

  protected def popStateHandler(ppe: PopStateEvent): Unit = {
    ppe.state match {
      case value if js.isUndefined(value) => dom.console.error("scroll to undefined id")
      case null => dom.console.error("scroll to null")
      case pos if !js.isUndefined(pos.dyn.id) =>
        scrollTo(pos.dyn.id.toString)

      case st =>
        val gid = st.toString
        scrollTo(gid)
    }
  }

  protected def subscribeScroller() = {
    dom.window.onpopstate = popStateHandler _
    //initScroller()
  }


  protected def initScroller(): JQueryScrollbar = {
    val params = new mCustomScrollbarParams(axis = "yx", advanced = new mCustomScrollbarAdvancedParams(true))
    println("trying scrollbar "+params)
    $(scrollPanel).mCustomScrollbar(params)
  }
}



@ScalaJSDefined
class ScrollPosition(val id: String, val currentPosition: Double) extends js.Object {

}
@js.native
@JSName("PopStateEvent")
class FixedPopStateEvent(val typeArg: String, override val state: js.Any) extends PopStateEvent
{

}
