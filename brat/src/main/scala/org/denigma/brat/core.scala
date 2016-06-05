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

  def post[T](name: String): Unit = js.native

  def on(message: String, handler: js.Function0[Unit]): BratDispatcher = js.native

  def on[T](message: String, handler: js.Function1[T, Unit]): BratDispatcher = js.native

  def on[T1, T2](message: String, handler: js.Function2[T1, T2, Unit]): BratDispatcher = js.native

  def on[T1, T2, T3](message: String, handler: js.Function3[T1, T2, T3, Unit]): BratDispatcher = js.native

  def on[T1, T2, T3, T4](message: String, handler: js.Function4[T1, T2, T3, T4, Unit]): BratDispatcher = js.native

  def on[T1, T2, T3, T4, T5](message: String, handler: js.Function5[T1, T2, T3, T4, T5, Unit]): BratDispatcher = js.native

  def on[T1, T2, T3, T4, T5, T6](message: String, handler: js.Function6[T1, T2, T3, T4, T5, T6, Unit]): BratDispatcher = js.native

  def on[T1, T2, T3, T4, T5, T6, T7](message: String, handler: js.Function7[T1, T2, T3, T4, T5, T6, T7, Unit]): BratDispatcher = js.native

  def on[T1, T2, T3, T4, T5, T6, T7, T8](message: String, handler: js.Function8[T1, T2, T3, T4, T5, T6, T7, T8, Unit]): BratDispatcher = js.native

  def on[T1, T2, T3, T4, T5, T6, T7, T8, T9](message: String, handler: js.Function9[T1, T2, T3, T4, T5, T6, T7, T8, T9, Unit]): BratDispatcher = js.native

  def on[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10](message: String, handler: js.Function10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Unit]): BratDispatcher = js.native

  def on[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11](message: String, handler: js.Function11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, Unit]): BratDispatcher = js.native

  def on[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12](message: String, handler: js.Function12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, Unit]): BratDispatcher = js.native



}

@JSName("AnnotatorUI")
@js.native
class AnnotatorUI(dispatcher: BratDispatcher, svg: SVGElement) extends js.Object
{

}


@JSName("VisualizerUI")
@js.native
class VisualizerUI(dispatcher: BratDispatcher, svg: SVGWrapper) extends js.Object
{

}