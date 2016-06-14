package org.denigma.nlp

import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.raw._
import rx.Var
import org.denigma.binding.extensions._

class ContentManager() extends BindableView
{
  override lazy val id: String = "main"

  lazy val elem: Element = dom.document.body

  val selections = Var(List.empty[org.scalajs.dom.raw.Range])

  override def bindView() = {
    super.bindView()
    dom.window.document.onselectionchange = onSelectionChange _
    println("CONTENT script is working!")
    selections.onChange{
      case ss=> println("selection changed to: "+ss)
    }
  }

  protected def onSelectionChange(event: Event) = {
    val selection: Selection = dom.window.getSelection()
    val count = selection.rangeCount
    if (count > 0) {
      val values = {
        for {
          i <- 0 until count
          range = selection.getRangeAt(i)
        } yield range
      }.toList
      selections() = values
    }
  }

}
