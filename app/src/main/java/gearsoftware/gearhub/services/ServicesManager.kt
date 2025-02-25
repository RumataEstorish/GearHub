package gearsoftware.gearhub.services

import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.serviceprovider.ServiceWebProxy
import gearsoftware.gearhub.services.data.ServicesRepository
import gearsoftware.gearhub.services.data.model.SapPermissionEntity
import toothpick.InjectConstructor

/**
 * Services list with params
 */
@InjectConstructor
class ServicesManager(
    private val servicesRepository: ServicesRepository
) {
    val services: Map<String, ServiceProxy>
        get() = servicesRepository.services

    val enabledServices: List<ServiceProxy>
        get() = servicesRepository.checkedServices.values
            .sortedByDescending { x -> x is ServiceWebProxy && x.isLoggedIn || x !is ServiceWebProxy }

    val availableProviders: List<ServiceProxy>
        get() = servicesRepository.notCheckedServices.values.toList()

    val permissionServices: List<ServiceProxy>
        get() = servicesRepository.permissionServices.values.toList()

    fun isServiceChecked(name: String): Boolean =
        services[name]?.checked ?: false
}
