package org.denigma.nlp

import org.denigma.binding.binders.NavigationBinder
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.controls.code.CodeBinder
import org.denigma.controls.login.AjaxSession
import org.denigma.nlp.annotator.AnnotatorView
import org.denigma.nlp.communication.WebSocketNLPTransport
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import rx._

import scala.scalajs.js.annotation.JSExport
@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override lazy val id: String = "main"

  lazy val elem: Element = dom.document.body

  val session = new AjaxSession()

  val connector: WebSocketNLPTransport = WebSocketNLPTransport("notebook", "guest" + Math.random() * 1000)

  val message: Var[String] = Var("")

  this.withBinders(me => List(new CodeBinder(me), new NavigationBinder(me)))

  @JSExport
  def main(): Unit = {

    this.bindView()

    connector.open()
  }

  /**
    * Register views
    */
  override lazy val injector = defaultInjector
    .register("annotator")((el, args) => new AnnotatorView(el, connector).withBinder(new CodeBinder(_)))


  @JSExport
  def load(content: String, into: String): Unit = {
    dom.document.getElementById(into).innerHTML = content
  }

  @JSExport
  def moveInto(from: String, into: String): Unit = {
    for {
      ins <- sq.byId(from)
      intoElement <- sq.byId(into)
    } {
      this.loadElementInto(intoElement, ins.innerHTML)
      ins.parentNode.removeChild(ins)
    }
  }

}
