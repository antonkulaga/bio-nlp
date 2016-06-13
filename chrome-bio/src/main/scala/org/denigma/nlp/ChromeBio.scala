package org.denigma.nlp

import chrome.browserAction.BrowserAction
import chrome.contextMenus.ContextMenus
import chrome.contextMenus.bindings.MenuInfo
import chrome.runtime.Runtime
import chrome.runtime.bindings.Port
import chrome.tabs.Tabs
import chrome.tabs.bindings.{Tab, TabCreateProperties}
import org.scalajs.dom

import scala.scalajs.js



object ChromeBio extends js.JSApp {

  def main(): Unit = {
    println("let the extension start!!!")
    addMenus()
    BrowserAction.addOnClick{
      case tab=>
        dom.window.alert("tab with url "+tab.url+" was clicked!")
    }
    Runtime.onStartup.listen(onStartup)
    Runtime.onConnect.listen(onConnect)
    Runtime.onConnectExternal.listen(onConnectExternal)
  }

  protected def selectionHandler(info: MenuInfo, tab:  Tab) = {
    val i = info
    val t = tab
    println("selection clicked")
  }

  protected def addMenus() = {
    ContextMenus.addOnClick(selectionHandler)
    val id = ContextMenus.create("nlpSelection", "Anylyze selection!", List("selection"))
    println("ID IS "+id)
  }



  def onStartup(fn: Unit): Unit =
  {
    println("started with"+fn)
    println("it should test context")
    addMenus()
  }

  def onConnect(port: Port) = {
    println("connected with a port " +port.name)
  }

  def onConnectExternal(port: Port) = {
    println("externally connected with a port " +port.name)
  }


}