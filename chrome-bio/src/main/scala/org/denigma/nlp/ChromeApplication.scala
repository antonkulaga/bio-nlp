package org.denigma.nlp

import chrome.app.runtime.bindings.LaunchData
import chrome.app.window.Window
import utils.ChromeApp

import scalajs.concurrent.JSExecutionContext.Implicits.queue

object ChromeBio extends ChromeApp {

  override def onLaunched(launchData: LaunchData): Unit = {
    println("hello world from scala!")
    Window.create("assets/html/App.html").foreach { window =>
      /**
      Access to the document of the newly created window.
         From here you can change the HTML of the window with whatever
         library you want to use.
        */
      window.contentWindow.document
    }
  }

}