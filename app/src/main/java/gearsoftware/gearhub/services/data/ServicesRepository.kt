package gearsoftware.gearhub.services.data

import android.content.SharedPreferences
import com.google.gson.Gson
import gearsoftware.gearhub.common.IPreferences
import gearsoftware.gearhub.common.objectListPreference
import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.serviceprovider.ServiceWebProxy
import gearsoftware.gearhub.services.SapPermission
import gearsoftware.gearhub.services.data.model.SapPermissionEntity
import gearsoftware.gearhub.services.data.model.ServiceUserLogin
import gearsoftware.gearhub.services.data.sources.ServicesInternalSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import toothpick.InjectConstructor

@InjectConstructor
class ServicesRepository(
    servicesInternalSource: ServicesInternalSource,
    override val sharedPreferences: SharedPreferences
) : IPreferences {
    companion object {
        private const val CHECKED_SERVICES = "CHECKED_SERVICES"
        private const val LOGGED_IN_SERVICES = "SERVICE_LOGINS"
        private const val SERVICE_USERS = "SERVICE_USERS"
    }

    val services: Map<String, ServiceProxy> =
        servicesInternalSource.services

    val serviceWeb: Map<String, ServiceWebProxy> =
        services.filter { it.value is ServiceWebProxy }.mapValues { it.value as ServiceWebProxy }

    val checkedServices: Map<String, ServiceProxy>
        get() = services.filterValues { it.checked }

    val notCheckedServices: Map<String, ServiceProxy>
        get() = services.filterValues { !it.checked }

    val permissionServices: Map<String, ServiceProxy>
        get() = services.filterValues { x -> x.permissions != null }

    init {

        sharedPreferences.getStringSet(CHECKED_SERVICES, setOf())
            ?.forEach {
                services[it]?.checked = true
            }
        sharedPreferences.getStringSet(LOGGED_IN_SERVICES, setOf())
            ?.forEach {
                (services[it] as? ServiceWebProxy)?.isLoggedIn = true
            }

        sharedPreferences.getStringSet(SERVICE_USERS, setOf())
            ?.forEach {
                Gson().fromJson(it, ServiceUserLogin::class.java)?.let { s ->
                    (services[s.serviceName] as? ServiceWebProxy)?.userName = s.userName
                }
            }
    }

    private fun writeCheckedServices() {
        sharedPreferences.edit().putStringSet(CHECKED_SERVICES,
            services.filter { it.value.checked }
                .map { it.key }
                .toSet())
            .apply()
    }

    private fun checkServices(serviceNames: List<String>, isChecked: Boolean): Completable =
        Observable.fromCallable { services.filterKeys { serviceNames.contains(it) } }
            .flatMap { Observable.fromIterable(it.values) }
            .doOnNext { it.checked = isChecked }
            .ignoreElements()
            .doOnComplete(::writeCheckedServices)

    fun checkService(service: ServiceProxy, isChecked: Boolean): Completable =
        checkServices(listOf(service.name), isChecked)

    fun setServiceIsLoggedIn(serviceName: String, isLoggedIn: Boolean) {
        serviceWeb[serviceName]?.let {
            it.isLoggedIn = isLoggedIn
            setServiceUserName(serviceName, "")
        }


        sharedPreferences.edit().putStringSet(LOGGED_IN_SERVICES,
            serviceWeb
                .filter { it.value.isLoggedIn }
                .map { it.key }
                .toSet())
            .apply()
    }

    fun setServiceUserName(serviceName: String, userName: String) {
        serviceWeb[serviceName]?.userName = userName
        sharedPreferences.edit().putStringSet(SERVICE_USERS,
            serviceWeb
                .filter { it.value.userName.isNotBlank() }
                .map { Gson().toJson(ServiceUserLogin(it.key, it.value.userName)) }
                .toSet())
            .apply()
    }
}