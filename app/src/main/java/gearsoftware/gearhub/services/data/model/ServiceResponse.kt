package gearsoftware.gearhub.services.data.model

sealed class ServiceResponse(open val serviceName: String) {
    data class ServiceUserName(val userName: String, override val serviceName: String) : ServiceResponse(serviceName)
    data class ServiceLoggedIn(override val serviceName: String) : ServiceResponse(serviceName)
    data class ServiceLoggedOut(override val serviceName: String) : ServiceResponse(serviceName)
}