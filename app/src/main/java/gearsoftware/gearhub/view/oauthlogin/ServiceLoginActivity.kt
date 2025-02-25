package gearsoftware.gearhub.view.oauthlogin

import android.content.Intent
import android.os.Bundle
import gearsoftware.gearhub.BaseActivity
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.servicecommunication.uiside.UiToServiceUC
import gearsoftware.gearhub.serviceprovider.AbstractSapService.Companion.SERVICE_NAME_EXTRA
import gearsoftware.gearhub.services.ServicesManager
import gearsoftware.gearhub.services.data.model.LoginResultStatus
import gearsoftware.gearhub.services.data.model.ServiceRequest
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.extension.getInstance

/**
 * Basic onLoginIntent activity
 */
abstract class ServiceLoginActivity : BaseActivity() {

    companion object {
        const val LOGIN_RESULT_EXTRA = "LOGIN_RESULT_EXTRA"
        const val LOGIN_RESULT_ERROR_EXTRA = "LOGIN_RESULT_ERROR_EXTRA"
    }

    private val compositeDisposable = CompositeDisposable()
    private lateinit var uiToServiceUC: UiToServiceUC
    private lateinit var serviceManager: ServicesManager

    protected var serviceName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KTP.openScopes(Scopes.APP)
            .apply {
                uiToServiceUC = getInstance()
                serviceManager = getInstance()
            }

        intent?.let {
            serviceName = it.getStringExtra(SERVICE_NAME_EXTRA)
        }
    }


    protected fun sendLoginResult(serviceName: String?, requestCode: Int, resultCode: Int, extras: Bundle?, data: String, status: LoginResultStatus) {
        uiToServiceUC(
            ServiceRequest.ServiceLoginResult(
                serviceManager.services[serviceName]!!,
                requestCode = requestCode,
                resultCode = resultCode,
                status = status,
                extras = extras,
                result = data
            )
        )
            .subscribeBy(onError = Timber::e)
            .addTo(compositeDisposable)
        finish()
    }

    protected fun sendLoginResult(serviceName: String?, requestCode: Int, resultCode: Int, intent: Intent?, status: LoginResultStatus) {
        sendLoginResult(
            serviceName, requestCode, resultCode, intent?.extras, if (intent?.hasExtra(LOGIN_RESULT_EXTRA) == true) {
                intent.getStringExtra(LOGIN_RESULT_EXTRA) ?: ""
            } else {
                intent?.getStringExtra(LOGIN_RESULT_ERROR_EXTRA) ?: ""
            }, status
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
