package org.denigma.nlp

import org.denigma.binding.binders.{GeneralBinder, NavigationBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.controls.code.CodeBinder
import org.denigma.controls.login.AjaxSession
import org.denigma.nlp.brat._
import org.denigma.nlp.messages.MessagesNLP
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import rx._
import rx.Ctx.Owner.Unsafe.Unsafe

import scala.concurrent.duration._
import scala.scalajs.js.annotation.JSExport
@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override lazy val id: String = "main"

  lazy val elem: Element = dom.document.body

  val session = new AjaxSession()

  val connector: WebSocketTransport = WebSocketTransport("notebook", "guest" + Math.random() * 1000)

  val message: Var[String] = Var("")


  this.withBinders(me => List(new CodeBinder(me), new NavigationBinder(me)))

  val text =
    """
      |Our results indicate that the transphosphorylation of an endogenous epidermal growth factor receptor (EGFR) in the human embryonic kidney (HEK-293) cell line does not occur when co-expressed delta-ORs are stimulated by the delta-opioid agonist, D-Ser-Leu-enkephalin-Thr (DSLET). Moreover, neither pre-incubation of cultures with the selective EGFR antagonist, AG1478, nor down-regulation of the EGFR to a point where EGF could no longer activate ERKs had an inhibitory effect on ERK activation by DSLET. These results appear to rule out any structural or catalytic role for the EGFR in the delta-opioid-mediated MAPK cascade. To confirm these results, we used C6 glioma cells, a cell line devoid of the EGFR. In delta-OR-expressing C6 glioma cells, opioids produce a robust phosphorylation of ERK 1 and 2, whereas EGF has no stimulatory effect. Furthermore, antagonists to the RTKs that are endogenously expressed in C6 glioma cells (insulin receptor (IR) and platelet-derived growth factor receptor (PDGFR)) were unable to reduce opioid-mediated ERK activation.
    """.stripMargin



  @JSExport
  def main(): Unit = {
    import scalajs.js.JSConverters._

    this.bindView()

    //println("LET US LOQD IN A NORMAL WAY")

    connector.onOpen.triggerLater{
      val mess = MessagesNLP.Annotate(text)
      println("sending "+ mess)
      message() = mess.toString
      connector.send(mess)
    }
    connector.open()
  }

  /**
    * Register views
    */
  override lazy val injector = defaultInjector
    .register("Login")((el, args) => new AnnotatorView(el, connector).withBinder(new GeneralBinder(_)))


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
