package org.denigma.brat.extensions

import org.denigma.brat._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

object BratCommands {
  val requestRenderData  = "requestRenderData"
  val collectionLoaded = "collectionLoaded"

}

class BratManager(val container: String, webFontURLs: List[String]) {

  val disp = new BratDispatcher()
  val visualizer = new BratVisualizer(disp, "annotation", webFontURLs.toJSArray)
  //val ui = new VisualizerUI(disp, visualizer.svg)
  //val annotator = new AnnotatorUI(disp, visualizer.svg)
  //println(visualizer.svg)

  def update(colData: ColData, docData: DocData) = {
    disp.post(BratCommands.collectionLoaded, js.Array(colData))
    disp.post(BratCommands.requestRenderData, js.Array(docData))
    disp
  }

}
