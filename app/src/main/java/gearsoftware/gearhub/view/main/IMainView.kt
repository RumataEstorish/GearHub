package gearsoftware.gearhub.view.main

import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.services.SapPermission
import gearsoftware.gearhub.view.main.model.PermissionType

/**
 * Main activity view
 */
interface IMainView {
    fun checkSASupport(): Boolean
    fun showAddButton(show: Boolean)
    fun showNotSupported()
    fun showInvite()
    fun showList()
    fun showProvidersSelection()
    fun requestPermission(permissions: List<String>)
    fun checkPermission(sapPermission: SapPermission): Boolean
    fun showPermissionNotGranted(name: String)
    fun showPermissionDialog(sapPermission: SapPermission)
    fun redirectToSettings()
    fun openLink(address: String)
    fun closeOpenLinkNotification()
    fun showPermissionNotGrantedDialog(service: ServiceProxy, permissionType: PermissionType)
}