package chrome.browserAction

import chrome.browserAction.bindings.TabDetails
import chrome.events.EventSource
import chrome.events.EventSourceImplicits._
import chrome.tabs.bindings.Tab

import scala.concurrent.{Future, Promise}

object BrowserAction {

  def onClicked: EventSource[Tab] = bindings.BrowserAction.onClicked

  def getPopup(tabDetails: TabDetails): Future[String] = {
    val p = Promise[String]
    def handler(result: String): Unit = {
      p.success(result)
    }
    chrome.browserAction.bindings.BrowserAction.getPopup(tabDetails, handler _)
    p.future
  }

}
