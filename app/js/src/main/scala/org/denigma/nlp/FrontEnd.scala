package org.denigma.nlp

import org.denigma.binding.binders.{GeneralBinder, NavigationBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.controls.code.CodeBinder
import org.denigma.controls.login.{AjaxSession, LoginView}
import org.denigma.nlp.brat.{BratUtil, ColData, DocData, EntityType}
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

  @JSExport
  def main(): Unit = {
    this.bindView()
    val bratLocation = "/resources/brat"
    val webFontURLs = Array(
    bratLocation + "/static/fonts/Astloch-Bold.ttf",
    bratLocation + "/static/fonts/PT_Sans-Caption-Web-Regular.ttf",
    bratLocation + "/static/fonts/Liberation_Sans-Regular.ttf"
    )

    val colData = new ColData(
      Array(
        new EntityType("Person", Array("Person", "Per"), "#7fa2ff", "darken")
      )
    )
    val docData = new DocData(
      "Ed O'Kelley was the man who shot the man who shot Jesse James.",
      Array(
        /* Format: [${ID}, ${TYPE}, [[${START}, ${END}]]]
        note that range of the offsets are [${START},${END}) */
        Array("T1", "Person", Array(Array(0, 11))),
        Array("T2", "Person", Array(Array(20, 23))),
        Array("T3", "Person", Array(Array(37, 40))),
        Array("T4", "Person", Array(Array(50, 61)))
    ))
    BratUtil.embed("annotation", colData, docData, webFontURLs)
    println("LET US LOQD IN A NORMAL WAY")
/*
    js.eval(
      """
        |var bratLocation = '/resources/brat';
        |        var webFontURLs = [
        |          bratLocation + '/static/fonts/Astloch-Bold.ttf',
        |          bratLocation + '/static/fonts/PT_Sans-Caption-Web-Regular.ttf',
        |          bratLocation + '/static/fonts/Liberation_Sans-Regular.ttf'
        |        ];
        |
        |
        |        var collData = {
        |          entity_types: [ {
        |            type   : 'Person',
        |            /* The labels are used when displaying the annotion, in this case
        |             we also provide a short-hand "Per" for cases where
        |             abbreviations are preferable */
        |            labels : ['Person', 'Per'],
        |            // Blue is a nice colour for a person?
        |            bgColor: '#7fa2ff',
        |            // Use a slightly darker version of the bgColor for the border
        |            borderColor: 'darken'
        |          } ]
        |        };
        |
        |        var docData = {
        |          // Our text of choice
        |          text     : "Ed O'Kelley was the man who shot the man who shot Jesse James.",
        |          // The entities entry holds all entity annotations
        |          entities : [
        |            /* Format: [${ID}, ${TYPE}, [[${START}, ${END}]]]
        |             note that range of the offsets are [${START},${END}) */
        |            ['T1', 'Person', [[0, 11]]],
        |            ['T2', 'Person', [[20, 23]]],
        |            ['T3', 'Person', [[37, 40]]],
        |            ['T4', 'Person', [[50, 61]]],
        |          ],
        |        };
        |
        |        collData['entity_attribute_types'] = [ {
        |          type  : 'Notorious',
        |          /* brat supports multi-valued attributes, but in our case we will only
        |           use a single value and add a glyph to the visualisation to indicate
        |           that the entity carries that attribute */
        |          values: { 'Notorious': { 'glyph': '★' } }
        |        } ];
        |
        |
        |        docData['attributes'] = [
        |          // Format: [${ID}, ${TYPE}, ${TARGET}]
        |          ['A1', 'Notorious', 'T4']
        |        ];
        |
        |
        |        collData['relation_types'] = [ {
        |          type     : 'Anaphora',
        |          labels   : ['Anaphora', 'Ana'],
        |          // dashArray allows you to adjust the style of the relation arc
        |          dashArray: '3,3',
        |          color    : 'purple',
        |          /* A relation takes two arguments, both are named and can be constrained
        |           as to which types they may apply to */
        |          args     : [
        |            //
        |            {role: 'Anaphor', targets: ['Person'] },
        |            {role: 'Entity',  targets: ['Person'] }
        |          ]
        |        } ];
        |
        |        docData['relations'] = [
        |          // Format: [${ID}, ${TYPE}, [[${ARGNAME}, ${TARGET}], [${ARGNAME}, ${TARGET}]]]
        |          ['R1', 'Anaphora', [['Anaphor', 'T2'], ['Entity', 'T1']]]
        |        ];
        |
        |
        |        collData['event_types'] = [ {
        |          type   : 'Assassination',
        |          labels : ['Assassination', 'Assas'],
        |          bgColor: 'lightgreen',
        |          borderColor: 'darken',
        |          /* Unlike relations, events originate from a span of text and can take
        |           several arguments */
        |          arcs   : [
        |            {type: 'Victim', labels: ['Victim','Vict'] },
        |            // Just like the event itself, its arguments can be styled
        |            {type: 'Perpetrator', labels: ['Perpetrator','Perp'], color: 'green' }
        |          ]
        |        } ];
        |
        |        /* Events also have trigger annotations, these are spans that are not
        |         visualised. This enables sharing of triggers when this is desirable, such
        |         as in the sentence "He robbed the bank and the post office", where
        |         "robbed" gives rice to two separate events that shares a single trigger */
        |        docData['triggers'] = [
        |          // The format is identical to that of entities
        |          ['T5', 'Assassination', [[45, 49]]],
        |          ['T6', 'Assassination', [[28, 32]]]
        |        ];
        |
        |        docData['events'] = [
        |          // Format: [${ID}, ${TRIGGER}, [[${ARGTYPE}, ${ARGID}], ...]]
        |          ['E1', 'T5', [['Perpetrator', 'T3'], ['Victim', 'T4']]],
        |          ['E2', 'T6', [['Perpetrator', 'T2'], ['Victim', 'T3']]]
        |        ];
        |
        |
        |        $( document ).ready(function() {
        |          Util.embed(
        |                  // id of the div element where brat should embed the visualisations
        |                  'annotation',
        |                  // object containing collection data
        |                  collData,
        |                  // object containing document data
        |                  docData,
        |                  // Array containing locations of the visualisation fonts
        |                  webFontURLs
        |          );
        |        });
        |
        |
        |
      """.stripMargin
    )
    */

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
