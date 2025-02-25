package gearsoftware.gearhub.view.main

import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.services.SapPermission

/**
 * Main presenter
 */
interface IMainPresenter {
    fun onServicesAdded(services: List<ServiceProxy>)
    fun onAddService()
    fun onServiceAdded(serviceName: String)
    fun onPermissionsNotGranted(permissions: List<String>)
    fun onCreated()
    fun onPause()
    fun onResume()
    fun onDestroy()
    fun onOpenLink(address: String?)
    fun onPermissionDialogCancel(permission: SapPermission)
    fun onPermissionDialogOk(permission: SapPermission)
    fun onGoToSettingsDialog(service: ServiceProxy)
    fun onRemoveServiceDialog(service: ServiceProxy)
}