package org.denigma.brat.extensions

/**
  * Created by antonkulaga on 6/2/16.
  */
object BratCommands extends CommentsCommands with DataCommands with RenderingCommands with EventsCommands{


  val abbrevs = "abbrevs"

  val isReloadOkay = "isReloadOkay"

  val messages = "messages"

  val configurationChanged = "configurationChanged"


}

trait RenderingCommands {
  val renderData = "renderData"

  val triggerRender = "triggerRender"
  val requestRenderData = "requestRenderData"

  val textBackgrounds = "textBackgrounds"
  val layoutDensity = "layoutDensity"
  val svgWidth = "svgWidth"
  val current = "current"

  val clearSVG = "clearSVG"
  val doneRendering = "doneRendering"
  val startedRendering = "startedRendering"
  val spin = "spin"
  val unspin = "unspin"
}

trait DataCommands {
  val collectionLoaded = "collectionLoaded"
  val collectionChanged = "collectionChanged"
  val newSourceData = "newSourceData"
  val resetData = "resetData"
  val spanAndAttributeTypesLoaded = "spanAndAttributeTypesLoaded"

}

trait EventsCommands {
  val mouseover = "mouseover"
  val mouseout = "mouseout"
  val arcDragArcDrawn = "arcDragArcDrawn"
}


trait CommentsCommands {
  val displaySpanComment = "displaySpanComment"
  val displaySentComment = "displaySentComment"
  val  displayArcComment = "displayArcComment"
  val hideComment = "hideComment"

}