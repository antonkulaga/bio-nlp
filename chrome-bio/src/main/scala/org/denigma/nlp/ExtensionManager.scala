package org.denigma.nlp
import chrome.browserAction.BrowserAction
import chrome.contextMenus.ContextMenus
import chrome.contextMenus.bindings.MenuInfo
import chrome.runtime.Runtime
import chrome.runtime.bindings.Port
import chrome.tabs.bindings.Tab
import org.scalajs.dom

class ExtensionManager() {

  def init() = {
    println("let the extension start!!!")
    addMenus()
    Runtime.onStartup.listen(onStartup)
    Runtime.onConnect.listen(onConnect)
    Runtime.onConnectExternal.listen(onConnectExternal)
  }

  protected def selectionHandler(event: (MenuInfo, Tab)) = event match {
    case (info: MenuInfo, tab:  Tab) => println("selection clicked")
  }

  protected def addMenus() = {
    ContextMenus.onClicked.listen(selectionHandler)
    val id = ContextMenus.create("nlpSelection", "Anylyze selection!", List("selection"))
    println("ID IS "+id)
  }


  protected def onStartup(fn: Unit): Unit =
  {
    println("started with"+fn)
    println("it should test context")
    addMenus()
  }

  protected def onConnect(port: Port) = {
    println("connected with a port " +port.name)
  }

  protected def onConnectExternal(port: Port) = {
    println("externally connected with a port " +port.name)
  }

}
