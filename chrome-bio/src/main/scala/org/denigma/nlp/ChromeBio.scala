package org.denigma.nlp

import chrome.app.runtime.bindings.LaunchData
import chrome.app.window.Window
import chrome.app.window.bindings.CreateWindowOptions
import chrome.browser.Browser
import chrome.tabs.Tabs
import org.scalajs.dom
import utils.ChromeApp
import scalatags.JsDom.all._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object ChromeBio extends ChromeApp {


  //Browser.openTab("http://samlib.ru/p/plotnikow_sergej_aleksandrowich/")

  override def onLaunched(launchData: LaunchData): Unit = {

    //Browser.openTab("http://samlib.ru/comment/p/plotnikow_sergej_aleksandrowich/witehnicheskoezadanie")

    val options = CreateWindowOptions(id = "MainWindow")
    Window.create("assets/html/App.html", options).foreach { window =>
      window.contentWindow.onload = (e: dom.Event) => {
        val doc = window.contentWindow.document
        println("BODY IS:\n"+ doc.body.outerHTML)
        Browser.openTab("http://samlib.ru/p/plotnikow_sergej_aleksandrowich/")
        doc.body.appendChild(h1("Hello world!").render)
      }
    }
  }

}