package chrome.contextMenus

import chrome.ChromeAPI
import chrome.permissions.APIPermission

/**
  * Created by antonkulaga on 07/06/16.
  */
object ContextMenus  extends ChromeAPI
{
  val ContextMenus = APIPermission("contextMenus", "permission to add items to content menus")
  val requiredPermissions: Set[APIPermission] = Set( APIPermission("contextMenus", "permission to add items to content menus"))
}
