package chrome.contextMenus

import chrome.ChromeAPI
import chrome.contextMenus.bindings.{CreateProperties, MenuInfo, UpdateProperties}
import chrome.permissions.APIPermission
import chrome.tabs.bindings.Tab

import scala.scalajs.js
import scala.scalajs.js.|
import scalajs.js.JSConverters._

/**
  * Created by antonkulaga on 07/06/16.
  */
object ContextMenus  extends ChromeAPI
{
  val ContextMenus = APIPermission("contextMenus", "permission to add items to content menus")

  def addOnClick(callback: (MenuInfo, Tab) => Unit): Unit = bindings.OnMenuClicked.addListener(callback)

  val requiredPermissions: Set[APIPermission] = Set(ContextMenus)

  def create(createProperties: CreateProperties): String | Int = bindings.ContextMenus.create(createProperties)

  def create(id: String, title: String, contexts: List[String] = List("all")): String | Int = create(bindings.CreateProperties(id, title, contexts = js.Array(contexts:_*)))

  def update(id: String | Int, properties: UpdateProperties): Unit = bindings.ContextMenus.update(id, properties )

  def remove(menuItemId: String | Int, callback: ()=>Unit): String | Int = bindings.ContextMenus.remove(menuItemId, callback)

  def removeAll(callback: ()=>Unit): Unit = bindings.ContextMenus.removeAll(callback)

}

