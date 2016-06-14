package org.denigma.nlp

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

object ChromeBio extends js.JSApp {

  lazy val manager = new ExtensionManager() //lazy so it starts only in background script

  @JSExport
  def main(): Unit = {
    manager.init()
  }

  lazy val optionsView = new OptionsView()

  @JSExport
  def options(): Unit = {
    optionsView.bindView()
  }

  lazy val contentManager = new ContentManager()

  @JSExport
  def content(): Unit = {
    contentManager.bindView()
  }

  lazy val popupView = new PopupView()

  @JSExport
  def popup(): Unit = {
    popupView.bindView()
  }

}