package gearsoftware.gearhub.di.providers

import android.content.Context
import gearsoftware.gearhub.servicesimpl.enotegear.ENoteGearServiceProvider
import gearsoftware.gearhub.serviceprovider.AbstractSapService
import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.serviceprovider.ServiceWebProxy
import gearsoftware.gearhub.services.SapPermissionService
import gearsoftware.gearhub.services.SapService
import gearsoftware.gearhub.services.SapServiceDescription
import gearsoftware.gearhub.services.data.ServiceList
import gearsoftware.gearhub.socialgear.SocialGearProviderService
import gearsoftware.gearhub.servicesimpl.squaregear.FourSquareAccessoryProvider
import gearsoftware.gearhub.servicesimpl.taskgear.TaskGearServiceProvider
import gearsoftware.gearhub.servicesimpl.todogear.TodoGearServiceProvider
import toothpick.InjectConstructor
import javax.inject.Provider
import kotlin.reflect.full.createType

@InjectConstructor
class InternalServicesProvider(
    private val context: Context
) : Provider<ServiceList> {
    override fun get(): ServiceList = ServiceList(
        getServiceProxy<ENoteGearServiceProvider>(),
        getServiceProxy<TaskGearServiceProvider>(),
        getServiceProxy<TodoGearServiceProvider>(),
        getServiceProxy<FourSquareAccessoryProvider>(),
        getServiceProxy<SocialGearProviderService>()/*,
            getServiceProxy<TransGearProviderService>()*/
    )

    private inline fun <reified T : AbstractSapService> getServiceProxy(): Pair<String, ServiceProxy> =
        T::class.annotations
            .let { annotations ->

                val sap: SapService = annotations.filterIsInstance<SapService>().first()
                val permissions: SapPermissionService? = annotations.filterIsInstance<SapPermissionService>().firstOrNull()
                val p = permissions?.sapPermission?.constructors?.first { c -> c.parameters.size == 1 && c.parameters[0].type == Context::class.createType() }?.call(context)

                val description: SapServiceDescription = sap.description.constructors.first { c -> c.parameters.size == 1 && c.parameters[0].type == Context::class.createType() }.call(context)
                sap.name to if (sap.haveAuthorization) {
                    ServiceWebProxy(sap.name, description.description, description.icon, T::class.java.canonicalName!!, sap.haveSettings, permissions = p, storeUrl = sap.storeUrl)
                } else {
                    ServiceProxy(sap.name, description.description, description.icon, T::class.java.canonicalName!!, sap.haveSettings, permissions = p, storeUrl = sap.storeUrl)
                }
            }
}