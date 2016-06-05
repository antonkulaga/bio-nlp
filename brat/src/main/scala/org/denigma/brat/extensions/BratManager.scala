package org.denigma.brat.extensions

import org.denigma.brat._
import org.scalajs.dom.{Element, ParentNode}
import org.denigma.binding.extensions._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.{HTMLElement, SVGElement}


class BratManager(val container: String, webFontURLs: List[String]) {

  val disp = new BratDispatcher()
  val visualizer = new BratVisualizer(disp, "annotation", webFontURLs.toJSArray)
  // val ui = new VisualizerUI(disp, visualizer.svg)
  //val annotator = new AnnotatorUI(disp, visualizer.svg)
  //println(visualizer.svg)

  protected val dataID = "data-span-id"

  def update(colData: ColData, docData: DocData): BratDispatcher = {
    disp.post(BratCommands.collectionLoaded, js.Array(colData))
    disp.post(BratCommands.requestRenderData, js.Array(docData))
    disp
  }

}
