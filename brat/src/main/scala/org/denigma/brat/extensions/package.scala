package org.denigma.brat

import org.querki.jquery.{JQuery, JQueryEventObject}
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.Element

import scala.scalajs.js

/**
  * Created by antonkulaga on 6/2/16.
  */
package object extensions {


  //TODO: look at real arugments of the events
  implicit class ExtendedDispatcher(val dispatcher: BratDispatcher) extends MouseBratDispatcher with CommentsBratDispatcher
  {

    def onCollectionChanged(collectionChanged: () =>Unit) = dispatcher.on(BratCommands.collectionChanged, collectionChanged)

    def onCollectionLoaded(collectionLoaded: () =>Unit) =  dispatcher.on(BratCommands.collectionLoaded, collectionLoaded)

    def onRenderData(renderData: () => Unit) = dispatcher.on(BratCommands.renderData, renderData)

    def onTriggerRender(triggerRender: () => Unit) = dispatcher.on(BratCommands.triggerRender, triggerRender)

    def onRequestRenderData(requestRenderData: ()=> Unit) =  dispatcher.on(BratCommands.requestRenderData, requestRenderData)

    def onIsReloadOkay(isReloadOkay: ()=> Unit) = dispatcher.on(BratCommands.isReloadOkay, isReloadOkay)

    def onRestData(resetData: ()=> Unit ) = dispatcher.on(BratCommands.resetData, resetData)

    def onSetAbbrevs(setAbbrevs: ()=> Unit ) = dispatcher.on(BratCommands.abbrevs, setAbbrevs)

    def onSetTextBackgrounds(setTextBackgrounds: () => Unit) = dispatcher.on(BratCommands.textBackgrounds, setTextBackgrounds)

    def onSetLayoutDensity(setLayoutDensity: () => Unit) = dispatcher.on(BratCommands.layoutDensity, setLayoutDensity)

    def onSetSVGWidth(setSvgWidth: () => Unit) = dispatcher.on(BratCommands.svgWidth, setSvgWidth)

    def onGotCurrent(gotCurrent: () => Unit) = dispatcher.on(BratCommands.current, gotCurrent)

    def onClearSVG(clearSVG: () => Unit) = dispatcher.on(BratCommands.clearSVG, clearSVG)

    def onDoneRendering(doneRendering: () =>Unit) = dispatcher.on(BratCommands.doneRendering, doneRendering)

  }

  case class DisplaySpanEvent(evt: MouseEvent, target: Element, id: String, spanType: String, attributeText: String, text: String,
                              commentText: js.UndefOr[String],
                              commentType: js.UndefOr[String],
                              normalization: js.UndefOr[js.Array[Any]]
                             )

  trait CommentsBratDispatcher extends ExtBratDispatcher {


    //def addDisplaySpanHandler(fun: DisplaySpanEvent => Unit)  = onDisplaySpanComment(tp=> (DisplaySpanEvent.apply _).tupled(tp))

    //onDisplaySpanComment(tp=> DisplaySpanEvent.apply(tp.to:_*))//dispatcher.on(BratCommands.displaySpanComment, fun)// = "displaySpanComment"

    /*
    dispatcher.post('displayArcComment', [
    evt, target, symmetric, arcId,
    originSpanId, originSpanType, role,
    targetSpanId, targetSpanType,
    commentText, commentType]);
    */
    def onDisplayArcComment(
                             fun: (JQueryEventObject, JQuery, js.UndefOr[Any], js.UndefOr[Any], String, String, String, String, String, js.UndefOr[String], js.UndefOr[String]) =>Unit) = dispatcher.on(BratCommands.displayArcComment, fun)

    //event: JQueryEventObject, target: JQuery,
    //id: String, span: String, attributes: js.Array[Any],
    //spanText: String, commentText: js.UndefOr[String], commentType: js.UndefOr[String], normalizations: Any
    def onDisplaySpanComment(fun: (
      JQueryEventObject, JQuery, String, String, js.Array[Any], String, js.UndefOr[String], js.UndefOr[String], js.Array[Any]
      ) => Unit)  = dispatcher.on(BratCommands.displaySpanComment, fun)


    def onDisplaySentComment(fun: (JQueryEventObject, JQuery, String, String) => Unit) = dispatcher.on(BratCommands.displaySentComment, fun)
    //[evt, target, comment.text, comment.type]

    def onArcDragArcDrawn(fun: org.scalajs.dom.svg.Path => Unit) = dispatcher.on(BratCommands.arcDragArcDrawn, fun)
  }

  trait MouseBratDispatcher extends ExtBratDispatcher {

    def onMouseOver(onMouseOver: JQueryEventObject=> Unit) = dispatcher.on(BratCommands.mouseover, onMouseOver)

    def onMouseOut(onMouseOut: JQueryEventObject => Unit) = dispatcher.on(BratCommands.mouseout, onMouseOut)

  }

  trait ExtBratDispatcher {
    def dispatcher: BratDispatcher
  }
}
