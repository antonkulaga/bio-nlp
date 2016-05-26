package org.denigma.brat.extensions

import org.denigma.brat.{BratDispatcher, BratVisualizer, ColData, DocData}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

object BratCommands {
  val requestRenderData  = "requestRenderData"
  val collectionLoaded = "collectionLoaded"

}

class BratManager(val container: String, webFontURLs: List[String]) {

  val disp = new BratDispatcher()
  val visualizer = new BratVisualizer(disp, "annotation", webFontURLs.toJSArray)

  def update(colData: ColData, docData: DocData) = {
    disp.post(BratCommands.collectionLoaded, js.Array(colData))
    disp.post(BratCommands.requestRenderData, js.Array(docData))
    disp
  }

}
