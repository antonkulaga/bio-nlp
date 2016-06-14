package org.denigma.nlp

import org.denigma.binding.binders.NavigationBinder
import org.denigma.binding.views.BindableView
import org.denigma.controls.code.CodeBinder
import org.denigma.nlp.annotator.AnnotatorView
import org.denigma.nlp.communication.WebSocketNLPTransport
import org.scalajs.dom
import org.scalajs.dom.Element
import rx.Var

import scala.scalajs.js.annotation.JSExport
class PopupView extends BindableView {
  println("DOCUMENT IS +"+dom.document.body)
  println("BODY IS +"+dom.document.body.outerHTML)

  override lazy val id: String = "popup"



  lazy val elem: Element = dom.document.body

  val connector: WebSocketNLPTransport = WebSocketNLPTransport("notebook", "guest" + Math.random() * 1000)

  val message: Var[String] = Var("")

  this.withBinders(me => List(new CodeBinder(me), new NavigationBinder(me)))

  override def bindView() = {
    super.bindView()
    connector.open()
  }

  /**
    * Register views
    */
  override lazy val injector = defaultInjector
    .register("annotator")((el, args) => new AnnotatorView(el, connector).withBinder(new CodeBinder(_)))

}
