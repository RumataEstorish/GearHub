package gearsoftware.gearhub.services.data.model

import android.os.Bundle
import gearsoftware.gearhub.serviceprovider.ServiceProxy

sealed class ServiceRequest(open val service: ServiceProxy) {
    data class ServiceAdded(
        override val service: ServiceProxy
    ) : ServiceRequest(service)

    data class ServiceRemoved(
        override val service: ServiceProxy
    ) : ServiceRequest(service)

    data class ServiceLogin(
        override val service: ServiceProxy
    ) : ServiceRequest(service)

    data class ServiceLogout(
        override val service: ServiceProxy
    ) : ServiceRequest(service)

    data class ServiceSettingsOpen(
        override val service: ServiceProxy
    ) : ServiceRequest(service)

    data class ServiceNotificationAction(
        override val service: ServiceProxy,
        val actionId: String,
        val actionData: String
    ) : ServiceRequest(service)

    data class ServiceLoginResult(
        override val service: ServiceProxy,
        val requestCode: Int,
        val resultCode: Int,
        val result: String?,
        val status: LoginResultStatus,
        val extras: Bundle?
    ) : ServiceRequest(service)
}