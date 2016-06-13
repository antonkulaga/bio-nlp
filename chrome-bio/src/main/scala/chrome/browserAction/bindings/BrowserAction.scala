package chrome.browserAction.bindings

import chrome.tabs.bindings.Tab

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

/**
  * https://developer.chrome.com/extensions/browserAction
  */
@js.native
@JSName("chrome.browserAction")
object BrowserAction extends js.Object {

  def setPopup(popupDetails: PopupDetails): Any = js.native

  def getPopup(tabDetails: TabDetails, callback: js.Function1[String, Unit]): Unit = js.native

}

@js.native
@JSName("chrome.browserAction.onClicked")
object OnBrowserActionClicked extends js.Object {

  def addListener(callback: js.Function1[Tab, Unit]): Unit = js.native

}

@ScalaJSDefined
class PopupDetails(val popup: String, tabid: js.UndefOr[Int] = js.undefined) extends TabDetails(tabid)

@ScalaJSDefined
class TabDetails(val tabid: js.UndefOr[Int] = js.undefined) extends js.Object

