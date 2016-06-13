package chrome.contextMenus.bindings

import chrome.tabs.bindings.Tab

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}
import scala.scalajs.js.|
import scala.scalajs.js.JSConverters._

object MenuContexts {

  val ALL = "all"
  val PAGE = "page"
  val FRAME = "frame"
  val SELECTION = "selection"
  val LINKE = "link"
  val EDITABLE = "editable"
  val IMAGE = "image"
  val VIDEO = "video"
  val AUDIO = "audio"
  val LAUNCHER = "launcher"
  val BROWSER_ACTION = "browser_action"
  val PAGE_ACTION = "page_action"
}

@js.native
@JSName("chrome.contextMenus")
object ContextMenus extends js.Object{

  def create(createProperties: CreateProperties): String | Int = js.native

  def update(id: String | Int, properties: UpdateProperties): Unit = js.native

  def remove(menuItemId: String | Int, callback: js.Function0[Unit]): String | Int = js.native

  def removeAll(callback: js.Function0[Unit]): Unit = js.native

}

@js.native
@JSName("chrome.contextMenus.onClicked")
object OnMenuClicked extends js.Object {
  def addListener(callback: js.Function2[MenuInfo, Tab, Unit]): Unit = js.native
}

@ScalaJSDefined
class UpdateProperties(
                        val `type`: String = "normal", //"normal", "checkbox", "radio", or "separator"
                        val title: String,
                        val checked: js.UndefOr[Boolean] = js.undefined,
                        val contexts: js.Array[String] = js.Array(MenuContexts.ALL),
                        val onclick: js.UndefOr[js.Function2[MenuInfo, Tab, Unit]],
                        val parentId: js.UndefOr[String | Int] = js.undefined,
                        val documentUrlPatterns: js.UndefOr[js.Array[String]] = js.undefined,
                        val targetUrlPatterns: js.UndefOr[js.Array[String]] = js.undefined,
                        val enabled: Boolean = true
                        //TODO: continue facade
                      ) extends js.Object

object CreateProperties {

  def apply(id: String, title: String, contexts: js.Array[String] = js.Array(MenuContexts.ALL)): CreateProperties =
  new CreateProperties(id = id, title = title , contexts = contexts)
}

/**
  * specified at
  * https://developer.chrome.com/extensions/contextMenus
  */
@ScalaJSDefined
class CreateProperties(
                     val `type`: String = "normal",
                     val id: String | Int,
                     val title: String,
                     val checked: js.UndefOr[Boolean] = js.undefined,
                     val contexts: js.Array[String] = js.Array(MenuContexts.ALL),
                     val onclick: js.UndefOr[js.Function2[MenuInfo, Tab, Unit]] = js.undefined,
                     val parentId: js.UndefOr[String | Int] = js.undefined,
                     val documentUrlPatterns: js.UndefOr[js.Array[String]] = js.undefined,
                     val targetUrlPatterns: js.UndefOr[js.Array[String]] = js.undefined,
                     val enabled: Boolean = true
                      //TODO: continue facade
                      ) extends js.Object

/*
integer or string	(optional) parentId
The ID of a parent menu item; this makes the item a child of a previously added item.

array of string	(optional) documentUrlPatterns
Lets you restrict the item to apply only to documents whose URL matches one of the given patterns. (This applies to frames as well.) For details on the format of a pattern, see Match Patterns.

array of string	(optional) targetUrlPatterns
Similar to documentUrlPatterns, but lets you filter based on the src attribute of img/audio/video tags and the href of anchor tags.

boolean	(optional) enabled
Since Chrome 20.

Whether this context menu item is enabled or disabled. Defaults to true.
 */

@js.native
trait  MenuInfo extends js.Object{
  val menuItemId: String | Int = js.native
  val parentMenuItemId: js.UndefOr[String | Int]
  val mediaType: js.UndefOr[String]
  val linkUrl: js.UndefOr[String]
  val srcUrl: js.UndefOr[String]
  val pageUrl: js.UndefOr[String]
  val frameUrl: js.UndefOr[String]
  val selectionText: js.UndefOr[String]
  val editable: Boolean
  val wasChecked: js.UndefOr[Boolean]
  val checked: js.UndefOr[Boolean]
}
