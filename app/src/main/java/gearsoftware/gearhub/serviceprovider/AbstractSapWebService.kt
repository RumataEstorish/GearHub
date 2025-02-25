package gearsoftware.gearhub.serviceprovider

import android.content.Intent
import gearsoftware.gearhub.services.data.model.ServiceRequest
import gearsoftware.gearhub.services.data.model.ServiceResponse
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber

/**
 * Service that accesses to web
 */
abstract class AbstractSapWebService(s: String) : AbstractSapService(s) {

    override fun onCreate() {
        super.onCreate()

        serviceToUiUC.onReceivedFromUI
            .filter { it.service.name == name }
            .subscribeBy(
                onNext = {
                    when (it) {
                        is ServiceRequest.ServiceLoginResult -> setLoginResult(it)
                        is ServiceRequest.ServiceLogin -> onLoginIntent()
                        is ServiceRequest.ServiceLogout -> onLogoutIntent()
                        is ServiceRequest.ServiceRemoved -> onLogoutIntent()
                        else -> Unit
                    }
                },
                onError = Timber::e
            )
            .addTo(compositeDisposable)
    }

    @JvmOverloads
    protected fun startLoginActivity(activityClass: Class<*>, data: Intent? = null) {
        val intent = Intent(this, activityClass)
        if (data != null) {
            intent.putExtras(data)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(SERVICE_NAME_EXTRA, name)
        startActivity(intent)
    }

    fun sendUserName(name: String) {
        serviceToUiUC(ServiceResponse.ServiceUserName(name, this.name))
            .subscribeBy(onError = Timber::e)
            .addTo(compositeDisposable)
    }

    abstract fun onLogoutIntent()

    abstract fun onLoginIntent()

    fun sendLoggedIn() {
        serviceToUiUC(ServiceResponse.ServiceLoggedIn(name))
            .subscribeBy(onError = Timber::e)
            .addTo(compositeDisposable)
    }

    fun sendLoggedOut() {
        serviceToUiUC(ServiceResponse.ServiceLoggedOut(name))
            .subscribeBy(onError = Timber::e)
            .addTo(compositeDisposable)
    }

    abstract fun setLoginResult(serviceLoginResult: ServiceRequest.ServiceLoginResult)
}
