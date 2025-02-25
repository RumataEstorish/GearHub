package gearsoftware.gearhub.serviceprovider

import android.app.Service
import android.content.Intent
import android.os.IBinder
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.servicecommunication.uiside.ReceiveNotificationActionUC
import gearsoftware.gearhub.serviceprovider.AbstractSapService.Companion.SERVICE_NAME_EXTRA
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider.Companion.NOTIFICATION_EXTRA
import gearsoftware.gearhub.services.ServicesManager
import gearsoftware.gearhub.services.data.model.ServiceRequest
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject

class NotificationsSignalService : Service() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val serviceNotificationsUseCase: ReceiveNotificationActionUC by inject()
    private val servicesManager: ServicesManager by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        KTP.openScopes(Scopes.APP)
            .inject(this)

        val serviceName = intent?.getStringExtra(SERVICE_NAME_EXTRA) ?: ""
        val actionId = intent?.action ?: ""
        val notificationExtra = intent?.getStringExtra(NOTIFICATION_EXTRA) ?: ""

        serviceNotificationsUseCase(
            ServiceRequest.ServiceNotificationAction(
                servicesManager.services[serviceName]
                    ?: error("No service found: $serviceName"), actionId, notificationExtra
            )
        )
            .subscribeBy(onError = Timber::e)
            .addTo(compositeDisposable)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? =
        null

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}