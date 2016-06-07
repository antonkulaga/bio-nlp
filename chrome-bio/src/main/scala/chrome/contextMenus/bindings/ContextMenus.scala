package chrome.contextMenus.bindings

import scala.scalajs.js
import scala.scalajs.js.annotation.{ScalaJSDefined, JSName}


@js.native
@JSName("chrome.contextMenus")
object ContextMenus extends js.Object{

  //def create()
  def createProperties(): Unit = js.native

}

//https://developer.chrome.com/extensions/contextMenus
@ScalaJSDefined
class CreateProperties(
                     val ItemType: String = "normal",
                     val id: String,
                     val title: String,
                     val checked: Boolean
                      //TODO: continue facade
                      ) extends js.Object