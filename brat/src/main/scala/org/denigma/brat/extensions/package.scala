package org.denigma.brat

/**
  * Created by antonkulaga on 6/2/16.
  */
package object extensions {


  //TODO: look at real arugments of the events
  implicit class ExtendedDispatcher(val dispatcher: BratDispatcher) {
    def onCollectionChanged(collectionChanged: () =>Unit) = dispatcher.on(BratCommands.collectionChanged, collectionChanged)
    def onCollectionLoaded(collectionLoaded: () =>Unit) =  dispatcher.on(BratCommands.collectionLoaded, collectionLoaded)
    def onRenderData(renderData: ()=>Unit) = dispatcher.on(BratCommands.renderData, renderData)
    def onTriggerRender(triggerRender: ()=>Unit) = dispatcher.on(BratCommands.triggerRender, triggerRender)
    def onRequestRenderData(requestRenderData: ()=>Unit) =  dispatcher.on(BratCommands.requestRenderData, requestRenderData)
    def onIsReloadOkay(isReloadOkay: ()=> Unit) = dispatcher.on(BratCommands.isReloadOkay, isReloadOkay)
    def onRestData(resetData: ()=>Unit) = dispatcher.on(BratCommands.resetData, resetData)
    def onSetAbbrevs(setAbbrevs: ()=>Unit) = dispatcher.on(BratCommands.abbrevs, setAbbrevs)
    def onSetTextBackgrounds(setTextBackgrounds: ()=>Unit) = dispatcher.on(BratCommands.textBackgrounds, setTextBackgrounds)
    def onSetLayoutDensity(setLayoutDensity: ()=>Unit) = dispatcher.on(BratCommands.layoutDensity, setLayoutDensity)
    def onSetSVGWidth(setSvgWidth: ()=>Unit) = dispatcher.on(BratCommands.svgWidth, setSvgWidth)
    def onGotCurrent(gotCurrent: ()=>Unit) = dispatcher.on(BratCommands.current, gotCurrent)
    def onClearSVG(clearSVG: () => Unit) = dispatcher.on(BratCommands.clearSVG, clearSVG)
    def onMouseOver(onMouseOver: ()=>Unit) = dispatcher.on(BratCommands.mouseover, onMouseOver)
    def onMouseOut(onMouseOut: ()=>Unit) = dispatcher.on(BratCommands.mouseout, onMouseOut)
    def onDoneRendering(doneRendering: ()=>Unit) = dispatcher.on(BratCommands.doneRendering, doneRendering)
  }
}
