package org.denigma.brat

import org.scalajs.dom.raw.{HTMLElement, SVGElement}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName


@JSName("Util")
@js.native
object BratUtil  extends js.Object {

  def embed(id: String = "annotation", collData: ColData, docData: DocData,  webFontURLs: js.Array[String]): Unit = js.native
}

@JSName("Visualizer")
@js.native
class BratVisualizer(dispatcher: BratDispatcher, container: String, webFontsURLs: js.Array[String]) extends js.Object {

  val svg: SVGWrapper = js.native
}

@JSName("SVGWrapper")
@js.native
class SVGWrapper extends js.Object{
  val _svg: SVGElement = js.native
  val _container: HTMLElement = js.native
}

@JSName("Dispatcher")
@js.native
class BratDispatcher extends js.Object{

  def post[T](name: String, data: js.Array[T]): Unit = js.native

  def on[T](message: String, handler: js.Function0[Unit]): BratDispatcher = js.native

}

@JSName("AnnotatorUI")
@js.native
class AnnotatorUI(dispatcher: BratDispatcher, svg: SVGElement) extends js.Object
{

}


@JSName("VisualizerUI")
@js.native
class VisualizerUI(dispatcher: BratDispatcher, svg: SVGElement) extends js.Object
{

}