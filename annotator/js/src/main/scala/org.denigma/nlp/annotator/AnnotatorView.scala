package org.denigma.nlp.annotator

import org.denigma.binding.binders.Events
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.brat.extensions.BratManager
import org.denigma.controls.code.CodeBinder
import org.denigma.nlp.communication.WebSocketNLPTransport
import org.denigma.nlp.messages._
import org.scalajs.dom.{Element, MouseEvent}
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._


class AnnotatorView(val elem: Element, val connector: WebSocketNLPTransport) extends BindableView {

  lazy val bratLocation = "/resources/brat"
  lazy val webFontURLs = List(
    bratLocation + "/static/fonts/Astloch-Bold.ttf",
    bratLocation + "/static/fonts/PT_Sans-Caption-Web-Regular.ttf",
    bratLocation + "/static/fonts/Liberation_Sans-Regular.ttf"
  )

  val defText =
    """
      |Our results indicate that the transphosphorylation of an endogenous epidermal growth factor receptor (EGFR) in the human embryonic kidney (HEK-293) cell line does not occur when co-expressed delta-ORs are stimulated by the delta-opioid agonist, D-Ser-Leu-enkephalin-Thr (DSLET). Moreover, neither pre-incubation of cultures with the selective EGFR antagonist, AG1478, nor down-regulation of the EGFR to a point where EGF could no longer activate ERKs had an inhibitory effect on ERK activation by DSLET. These results appear to rule out any structural or catalytic role for the EGFR in the delta-opioid-mediated MAPK cascade. To confirm these results, we used C6 glioma cells, a cell line devoid of the EGFR. In delta-OR-expressing C6 glioma cells, opioids produce a robust phosphorylation of ERK 1 and 2, whereas EGF has no stimulatory effect. Furthermore, antagonists to the RTKs that are endogenously expressed in C6 glioma cells (insulin receptor (IR) and platelet-derived growth factor receptor (PDGFR)) were unable to reduce opioid-mediated ERK activation.
    """.stripMargin

  val text = Var(defText)

  val message = Var("")

  val ready = connector.NLPready

  val send: Var[MouseEvent] = Var(Events.createMouseEvent())
  send.triggerLater{
      val mess = MessagesNLP.Annotate(text.now)
      connector.send(mess)
    }

  connector.input.onChange{
    case inp @ MessagesNLP.DocumentAnnotations(doc, mentions) =>
      message() = pprint.tokenize(inp.toString, width = 300).mkString("\n")
      annotations() = inp

    case inp =>
      message() = inp.toString
  }

  val annotations: Var[MessagesNLP.DocumentAnnotations] = Var( MessagesNLP.DocumentAnnotations.empty)

  val bratManager = new ReachBratManager("annotation", webFontURLs, annotations)

  override lazy val injector = defaultInjector
    .register("annotations")((el, args) => new AnnotationsView(el, bratManager.elements).withBinder(new CodeBinder(_)))


}
