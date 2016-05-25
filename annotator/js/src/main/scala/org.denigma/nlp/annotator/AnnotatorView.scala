package org.denigma.nlp.annotator

import org.denigma.nlp.messages.MessagesNLP.DocumentAnnotations
import org.denigma.nlp.messages._
import org.denigma.binding.binders.Events
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.brat._
import org.denigma.controls.code.CodeBinder
import org.denigma.nlp.communication.WebSocketNLPTransport
import org.scalajs.dom.{Element, MouseEvent}
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._

import scala.scalajs.js.JSConverters._

class AnnotatorView(val elem: Element, val connector: WebSocketNLPTransport) extends BindableView {



  val defText =
    """
      |Our results indicate that the transphosphorylation of an endogenous epidermal growth factor receptor (EGFR) in the human embryonic kidney (HEK-293) cell line does not occur when co-expressed delta-ORs are stimulated by the delta-opioid agonist, D-Ser-Leu-enkephalin-Thr (DSLET). Moreover, neither pre-incubation of cultures with the selective EGFR antagonist, AG1478, nor down-regulation of the EGFR to a point where EGF could no longer activate ERKs had an inhibitory effect on ERK activation by DSLET. These results appear to rule out any structural or catalytic role for the EGFR in the delta-opioid-mediated MAPK cascade. To confirm these results, we used C6 glioma cells, a cell line devoid of the EGFR. In delta-OR-expressing C6 glioma cells, opioids produce a robust phosphorylation of ERK 1 and 2, whereas EGF has no stimulatory effect. Furthermore, antagonists to the RTKs that are endogenously expressed in C6 glioma cells (insulin receptor (IR) and platelet-derived growth factor receptor (PDGFR)) were unable to reduce opioid-mediated ERK activation.
    """.stripMargin

  val text = Var(defText)

  val message = Var("")

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

  override def bindView() = {
    super.bindView()
    addBrat()
  }

  protected def addBrat() = {
    embed(initColdData(), initDocData())
  }

  def embed(colData:  ColData, docData: DocData) = {
    val bratLocation = "/resources/brat"
    val webFontURLs = Array(
      bratLocation + "/static/fonts/Astloch-Bold.ttf",
      bratLocation + "/static/fonts/PT_Sans-Caption-Web-Regular.ttf",
      bratLocation + "/static/fonts/Liberation_Sans-Regular.ttf"
    )
    BratUtil.embed("annotation", colData, docData, webFontURLs.toJSArray)
  }

  val ready = connector.NLPready
  val annotations: Var[DocumentAnnotations] = Var(MessagesNLP.DocumentAnnotations.empty)

  protected def mention2Part(mention: Annotations.Mention, doc: Annotations.Document) = {
    val sentence = doc.sentences(mention.sentenceNum)
    val offset = sentence.startOffsets.head
    Entity(mention.hashCode().toString, mention.label, List((mention.start+offset, mention.end+offset)))
  }

  protected def bioColData() = {
    val types = List(
      new EntityType("Cellular_component", List("Cellular_component", "CellComp", "CC"), "lightgreen", "darken"),
      new EntityType("Simple_chemical", List("Simple_chemical", "Chemical", "Chem"), "pink", "darken"),
      new EntityType("Site", List("Site", "Si"), "gold", "darken"),
      new EntityType("Gene_or_gene_product", List("Gene_or_gene_product", "Gene_GP", "GGP"), "blue", "darken"),
      new EntityType("Protein", List("Protein", "Pro", "P"), "violet", "darken"),
      new EntityType("Complex", List("Protein_Complex", "Complex", "Cplx"), "navy", "darken")
    )

  }

  protected def changeBrat(annots: DocumentAnnotations) = if(annots!=MessagesNLP.DocumentAnnotations.empty){
    implicit val doc = annots.document
    val text = annots.document.text.getOrElse("")
    val mens = annots.mentions
    val parts: List[Entity] = mens.map{ case men=> mention2Part(men, doc)}
    val docs = new DocData( text, parts, Nil, Nil, Nil, Nil)
    //embed(ColData.)
  }


  val send: Var[MouseEvent] = Var(Events.createMouseEvent())
  send.triggerLater{
    println("let is send something!")
      val mess = MessagesNLP.Annotate(text.now)
      connector.send(mess)
    }

  connector.input.onChange{
    case inp @ MessagesNLP.DocumentAnnotations(doc, mentions) =>
      message() = pprint.tokenize(inp.toString, width = 300).mkString("\n")
      annotations() = inp
      changeBrat(inp)
      println(inp.toString)

    case inp =>
      message() = inp.toString
      println(inp.toString)
  }

  override lazy val injector = defaultInjector
    .register("annotations")((el, args) => new AnnotationsView(el, annotations).withBinder(new CodeBinder(_)))


}
