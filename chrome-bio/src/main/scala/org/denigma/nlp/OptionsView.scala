package org.denigma.nlp

import org.denigma.binding.binders.{GeneralBinder, NavigationBinder}
import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom.Element
import rx.Var
class OptionsView() extends BindableView
{

  println("DOCUMENT IS +"+dom.document.body)
  println("BODY IS +"+dom.document.body.outerHTML)

  override lazy val id: String = "options"

  lazy val elem: Element = dom.document.body

  //val connector: WebSocketNLPTransport = WebSocketNLPTransport("notebook", "guest" + Math.random() * 1000)

  val message: Var[String] = Var("")

  /**
    * Register views
    */
  override lazy val injector = defaultInjector
    //.register("annotator")((el, args) => new AnnotatorView(el, connector).withBinder(new CodeBinder(_)))


  override def bindView() = {
    super.bindView()
    println("OPTIONS ACTIVATED!")
  }

  this.withBinders(me => List(new GeneralBinder(me), new NavigationBinder(me)))

}
