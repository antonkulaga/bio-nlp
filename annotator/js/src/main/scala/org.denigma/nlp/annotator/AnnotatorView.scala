package org.denigma.nlp.annotator

import org.denigma.binding.binders.Events
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.brat.extensions._
import org.denigma.controls.code.CodeBinder
import org.denigma.nlp.annotator
import org.denigma.nlp.annotator.Application.WaitingServer
import org.denigma.nlp.communication.WebSocketNLPTransport
import org.denigma.nlp.messages.Annotations.Mention
import org.denigma.nlp.messages.MessagesNLP.KeepAlive
import org.denigma.nlp.messages._
import org.denigma.nlp.scrollbar.ScrollerView
import org.querki.jquery.{JQuery, JQueryEventObject}
import org.scalajs.dom
import org.scalajs.dom.raw.SVGElement
import org.scalajs.dom.{Element, MouseEvent}
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._

import scala.scalajs.js

object Application {
  sealed trait Status
  case object WaitingConnection extends Status
  case object WaitingServer extends Status
  case class Annotating(text: String) extends Status
  case object Ready extends Status

}


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

  val caption = Var()

  //val ready = connector.NLPready

  val status: Var[Application.Status] = Var(Application.WaitingServer)


  val waitingConnection = status.map{
    case Application.WaitingConnection => true
    case _ => false
  }

  val waitingServer = status.map{
    case Application.WaitingServer => true
    case _ => false
  }

  val ready = status.map{
    case Application.Ready => true
    case _ => false
  }

  val annotating = status.map{
    case Application.Annotating(_) => true
    case _ => false
  }

  val send: Var[MouseEvent] = Var(Events.createMouseEvent())
  send.triggerLater{
      val mess = MessagesNLP.Annotate(text.now)
      connector.send(mess)
      status() = Application.Annotating(text.now)
    }



  protected def subscribeEvents() = {
    if(connector.connected.now) status() = Application.WaitingServer else
      if(connector.NLPready.now) status() = Application.Ready else
        status() = Application.WaitingConnection

    connector.input.onChange{


      case MessagesNLP.NLPReady(uname)  =>
        status() = Application.Ready

      case con: MessagesNLP.Connected =>
        if(connector.NLPready.now) {
          dom.console.error("Connection and ready to run server in the same time!")
        } else status() = Application.WaitingServer

      case con: MessagesNLP.Disconnected =>
        status() = Application.WaitingConnection


      case inp @ MessagesNLP.DocumentAnnotations(doc, mentions) =>
        message() = pprint.tokenize(inp.toString, width = 300).mkString("\n")
        annotations() = inp
        status() = Application.Ready

      case KeepAlive(username) => //do nothing

      case inp =>
        message() = inp.toString
    }
  }

  protected def subscribeBrat() = {

    bratManager.disp.onDisplaySpanComment {
      case (event, target, id, span, attrib, text, comment, commentType, normalization) =>
        println(event, target, id, span, attrib, text, comment, commentType, normalization)
      //js.debugger()
    }

    bratManager.disp.onDisplayArcComment{
      case params =>
        //val arc: Tuple11[MouseEvent, ViewElement, Boolean, String, String, String, Any, Any, Any, Any, Any] = js.Tuple11.toScalaTuple11(tuple)
        println("ARC COMMENT DISPLAY \n " + params)
      //scalajs.js.debugger()
    }

    bratManager.disp.onDisplaySentComment{
      case sent =>
        println("display SENT comment" + sent)
        //scalajs.js.debugger()
    }
  }

  val annotations: Var[MessagesNLP.DocumentAnnotations] = Var( MessagesNLP.DocumentAnnotations.empty)

  lazy val bratManager = new ReachBratManager("annotation", webFontURLs, annotations)
  lazy val elements: Rx[Map[Mention, (String, SVGElement)]] = bratManager.elements
  import scalajs.js.JSConverters._

  override def bindView(): Unit = {
    super.bindView()
    subscribeEvents()
    subscribeBrat()
  }


 override lazy val injector = defaultInjector
   .register("annotations")((el, args) => new AnnotationsView(el, elements).withBinder(new CodeBinder(_)))

}
