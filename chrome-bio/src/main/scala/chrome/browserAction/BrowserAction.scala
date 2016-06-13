package chrome.browserAction

import chrome.browserAction.bindings.TabDetails
import chrome.tabs.bindings.Tab

import scala.concurrent.{Future, Promise}

object BrowserAction {

  def addOnClick(callback: (Tab) => Unit): Unit = bindings.OnBrowserActionClicked.addListener(callback)

  def getPopup(tabDetails: TabDetails): Future[String] = {
    val p = Promise[String]
    def handler(result: String): Unit = {
      p.success(result)
    }
    chrome.browserAction.bindings.BrowserAction.getPopup(tabDetails, handler _)
    p.future
  }

}
