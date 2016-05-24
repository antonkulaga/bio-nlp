package org.denigma.nlp

import org.denigma.binding.binders.{GeneralBinder, NavigationBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.controls.code.CodeBinder
import org.denigma.controls.login.{AjaxSession, LoginView}
import org.denigma.nlp.brat._
import org.scalajs.dom
import org.scalajs.dom.UIEvent
import org.scalajs.dom.raw.{Element, HTMLElement}
import rx.Ctx.Owner.Unsafe.Unsafe

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override lazy val id: String = "main"

  lazy val elem: Element = dom.document.body

  val session = new AjaxSession()

  val connector: WebSocketTransport = WebSocketTransport("notebook", "guest" + Math.random() * 1000)


  this.withBinders(me => List(new GeneralBinder(me), new NavigationBinder(me)))

  val text =
    """
      |RESULTS:
      |Our results indicate that the transphosphorylation of an endogenous epidermal growth factor receptor (EGFR) in the human embryonic kidney (HEK-293) cell line does not occur when co-expressed delta-ORs are stimulated by the delta-opioid agonist, D-Ser-Leu-enkephalin-Thr (DSLET). Moreover, neither pre-incubation of cultures with the selective EGFR antagonist, AG1478, nor down-regulation of the EGFR to a point where EGF could no longer activate ERKs had an inhibitory effect on ERK activation by DSLET. These results appear to rule out any structural or catalytic role for the EGFR in the delta-opioid-mediated MAPK cascade. To confirm these results, we used C6 glioma cells, a cell line devoid of the EGFR. In delta-OR-expressing C6 glioma cells, opioids produce a robust phosphorylation of ERK 1 and 2, whereas EGF has no stimulatory effect. Furthermore, antagonists to the RTKs that are endogenously expressed in C6 glioma cells (insulin receptor (IR) and platelet-derived growth factor receptor (PDGFR)) were unable to reduce opioid-mediated ERK activation.
    """.stripMargin

  protected def initColdData(): ColData = {
    val types =  List( new EntityType("Person", Array("Person", "Per"), "#7fa2ff", "darken") )
    val relationTypes = List(
      new RelationType("Anaphora", List("Anaphora", "Ana"),"3,3", "purple",
        List(
          new RelationRole("Anaphor", List("Person")),
          new RelationRole("Entity", List("Person"))
        )
      )
    )
    val events  = List(
      new EventType("Assassination", List("Assassination", "Assas"), "lightgreen", "darken",
        List(
          new LabeledType("Victim", List("Victim", "Vict")),
          new LabeledType("Perpetrator", List("Perpetrator", "Perp"))
        ))
    )
    val typeAttributes = List(new EntityAttributeType("Notorious", """{ "Notorious": { "glyph": "★" } }"""))
    new ColData(  types, relationTypes, typeAttributes, events)
  }

  protected def initDocData(): DocData = {
    val text = "Ed O'Kelley was the man who shot the man who shot Jesse James."
    val parts = List(
      Entity("T1", "Person", List((0, 11))),
      Entity("T2", "Person", List((20, 23))),
      Entity("T3", "Person", List((37, 40))),
      Entity("T4", "Person", List((50, 61)))
    )
    val atribs =  List(DocAttribute("A1", "Notorious", "T4"))
    val relations = List(Relation("R1", "Anaphora", "Anaphor", "T2", "Entity", "T1"))
    val events = List(
      BratEvent("E1", "T5", List("Perpetrator"->"T3", "Victim"->"T4")),
      BratEvent("E2", "T6", List("Perpetrator"->"T2", "Victim"->"T3"))
    )
    val triggers = List(
      Entity("T5", "Assassination", List((45, 49))),
      Entity("T6", "Assassination", List((28, 32)))
    )
    new DocData( text, parts, atribs, relations, events, triggers)
  }

  @JSExport
  def main(): Unit = {
    import scalajs.js.JSConverters._

    this.bindView()
    val bratLocation = "/resources/brat"
    val webFontURLs = Array(
    bratLocation + "/static/fonts/Astloch-Bold.ttf",
    bratLocation + "/static/fonts/PT_Sans-Caption-Web-Regular.ttf",
    bratLocation + "/static/fonts/Liberation_Sans-Regular.ttf"
    )
    val colData = initColdData()
    val docData = initDocData()
    BratUtil.embed("annotation", colData, docData, webFontURLs.toJSArray)
    println("LET US LOQD IN A NORMAL WAY")

    connector.open()
    connector.send(MessagesNLP.Annotate(text))
  }

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
