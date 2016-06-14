package chrome.contextMenus

import chrome.ChromeAPI
import chrome.contextMenus.bindings.{CreateProperties, MenuInfo, UpdateProperties}
import chrome.events.EventSource
import chrome.events.EventSourceImplicits._
import chrome.permissions.APIPermission
import chrome.tabs.bindings.Tab

import scala.concurrent.{Future, Promise}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.|

object ContextMenus  extends ChromeAPI
{
  val ContextMenus = APIPermission("contextMenus", "permission to add items to content menus")

  def onClicked: EventSource[(MenuInfo, Tab)] = bindings.ContextMenus.onClicked

  val requiredPermissions: Set[APIPermission] = Set(ContextMenus)

  def create(createProperties: CreateProperties): String | Int = bindings.ContextMenus.create(createProperties)

  def create(id: String, title: String, contexts: List[String]): String | Int = create(bindings.CreateProperties(id, title, contexts = js.Array(contexts:_*)))

  def update(id: String | Int, properties: UpdateProperties): Unit = bindings.ContextMenus.update(id, properties )

  def remove(menuItemId: String | Int, callback: () => Unit): Future[String | Int] = {
    val promise = Promise[Unit]
    val result = bindings.ContextMenus.remove(menuItemId, js.Any.fromFunction0[Unit](()=> {
      promise.success(Unit)
    }))
    promise.future.map(_=>result)
  }

  def removeAll(): Future[Unit] = {
    val promise = Promise[Unit]
    bindings.ContextMenus.removeAll(js.Any.fromFunction0[Unit](()=> {
      promise.success(Unit)
    }))
    promise.future
  }

}

