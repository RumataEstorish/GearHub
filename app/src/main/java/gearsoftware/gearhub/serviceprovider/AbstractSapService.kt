package gearsoftware.gearhub.serviceprovider

import android.app.Notification
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.core.app.ServiceCompat
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.servicecommunication.serviceside.ServiceToUiUC
import gearsoftware.gearhub.servicecommunication.uiside.ReceiveNotificationActionUC
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider.Companion.FOREGROUND_CANCEL_CONNECT_ACTION
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider.Companion.FOREGROUND_CANCEL_LOCATION_ACTION
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider.Companion.FOREGROUND_CANCEL_NETWORK_ACTION
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider.Companion.FOREGROUND_CANCEL_SEND_ACTION
import gearsoftware.gearhub.services.SapService
import gearsoftware.gearhub.services.ServicesManager
import gearsoftware.gearhub.services.data.model.ServiceRequest
import gearsoftware.sap.data.GearCommands
import gearsoftware.sap.data.GearStates
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.extension.getInstance

/**
 * Base service provider class
 */
abstract class AbstractSapService(
    s: String
) : gearsoftware.sap.Sap(s) {

    companion object {
        const val SERVICE_NAME_EXTRA = "SERVICE_NAME_EXTRA"

        const val FOREGROUND_NET_ID = 1001
        const val FOREGROUND_LOCATION_ID = 1002
        const val FOREGROUND_CONNECTION_ID = 1003
        const val FOREGROUND_OPEN_LINK_ID = 1004
        const val FOREGROUND_SEND_TEXT_ID = 1005

        const val FOREGROUND_CHANNEL_ID = "MAIN_CHANNEL"
        const val FOREGROUND_NET_CHANNEL_ID = "NET_CHANNEL"
        const val FOREGROUND_LOCATION_CHANNEL_ID = "LOCATION_CHANNEL"
        const val FOREGROUND_CONNECTION_CHANNEL_ID = "CONNECTION_CHANNEL"
        const val FOREGROUND_OPEN_LINK_CHANNEL_ID = "OPEN_LINK_CHANNEL"
        const val FOREGROUND_SEND_DATA_TO_WATCH_CHANNEL_ID = "SEND_DATA_TO_WATCH"

        const val SETTINGS_RESULT_ACTION = "gearsoftware.gearhub.SETTINGS_RESULT_ACTION"
        const val SETTINGS_SERVICE_NAME = "gearsoftware.gearhub.SETTINGS_SERVICE_NAME"
        const val SERVICE_SETTINGS_ACTION = "gearsoftware.gearhub.SERVICE_SETTINGS_ACTION"

    }

    private val runningNotifications: MutableList<Int> = mutableListOf()

    protected val compositeDisposable = CompositeDisposable()
    val name: String = this::class.annotations
        .filterIsInstance<SapService>()
        .first()
        .name

    private lateinit var serviceManager: ServicesManager
    protected lateinit var serviceToUiUC: ServiceToUiUC
        private set

    protected lateinit
    var serviceNotificationReceiver: ReceiveNotificationActionUC
        private set

    val isChecked: Boolean
        get() = serviceManager.isServiceChecked(name)

    protected fun startForegroundEx(foregroundId: Int, notification: Notification) {
        runningNotifications.add(foregroundId + name.hashCode())
        startForeground(foregroundId + name.hashCode(), notification)
    }

    @Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")
    protected fun stopForegroundEx(foregroundId: Int, removeNotification: Boolean) {
        runningNotifications.remove(foregroundId + name.hashCode())

        if (runningNotifications.isEmpty()) {
            getNotificationProvider().cancelAll()
            ServiceCompat.stopForeground(
                this,
                if (removeNotification) {
                    ServiceCompat.STOP_FOREGROUND_REMOVE
                } else {
                    ServiceCompat.STOP_FOREGROUND_DETACH
                }
            )
        } else {
            getNotificationProvider().cancelNotification(foregroundId)
        }
    }

    override fun onCreate() {
        super.onCreate()

        val scope = KTP.openScopes(Scopes.APP)
        serviceManager = scope.getInstance()
        serviceToUiUC = scope.getInstance()

        startForegroundEx(
            foregroundId = FOREGROUND_CONNECTION_ID,
            notification = getNotificationProvider().getConnectionToWatchNotification().build()
        )

        onGearState
            .subscribeBy(
                onNext = {
                    when (it) {
                        is GearStates.ConnectingStarted ->
                            startForegroundEx(
                                foregroundId = FOREGROUND_CONNECTION_ID,
                                notification = getNotificationProvider().getConnectionToWatchNotification().build()
                            )

                        is GearStates.Connected,
                        is GearStates.ConnectingStopped -> stopForegroundEx(
                            foregroundId = FOREGROUND_CONNECTION_ID,
                            removeNotification = true
                        )

                        is GearStates.SendingStarted ->
                            startForegroundEx(
                                foregroundId = FOREGROUND_SEND_TEXT_ID,
                                notification = getNotificationProvider().getSendingDataToWatchNotification().build()
                            )

                        is GearStates.SendingStopped -> stopForegroundEx(
                            foregroundId = FOREGROUND_SEND_TEXT_ID,
                            true
                        )

                        is GearStates.LocationRequestStarted ->
                            startForegroundEx(
                                foregroundId = FOREGROUND_LOCATION_ID,
                                notification = getNotificationProvider().getLocationNotification().build()
                            )

                        is GearStates.LocationRequestStopped -> stopForegroundEx(
                            foregroundId = FOREGROUND_LOCATION_ID,
                            removeNotification = true
                        )

                        is GearStates.NetworkRequestStarted ->
                            startForegroundEx(
                                foregroundId = FOREGROUND_NET_ID,
                                notification = getNotificationProvider().getNetworkNotification().build()
                            )

                        is GearStates.NetworkRequestStopped -> stopForegroundEx(
                            foregroundId = FOREGROUND_NET_ID,
                            removeNotification = true
                        )

                        is GearStates.PeerAgentDisconnected -> stopForegroundEx(
                            foregroundId = FOREGROUND_CONNECTION_ID,
                            removeNotification = true
                        )

                        else -> Unit
                    }
                },
                onError = Timber::e
            ).addTo(compositeDisposable)

        onGearCommand
            .subscribeBy(
                onNext = {
                    when (it) {
                        is GearCommands.OpenWebPage -> getNotificationProvider().notify(FOREGROUND_OPEN_LINK_ID, getNotificationProvider().getOpenLinkNotification(it.address).build())
                        else -> Unit
                    }
                }, onError = Timber::e
            )
            .addTo(compositeDisposable)

        serviceToUiUC.onReceivedFromUI
            .filter { it.service.name == name }
            .subscribeBy(
                onNext = {
                    when (it) {
                        is ServiceRequest.ServiceAdded -> onAddIntent()
                        is ServiceRequest.ServiceRemoved -> {
                            onRemoveIntent()
                            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                            stopSelf()
                        }

                        is ServiceRequest.ServiceNotificationAction -> onServiceNotification(it.actionId, it.actionData)
                        else -> Unit
                    }
                },
                onError = Timber::e
            ).addTo(compositeDisposable)
    }

    open fun onIntent(intent: Intent) {}

    open fun onAddIntent() {}

    open fun onRemoveIntent() {}

    @CallSuper
    open fun onServiceNotification(actionId: String, data: String) {
        when (actionId) {
            FOREGROUND_CANCEL_CONNECT_ACTION -> stopConnectToWatch()
            FOREGROUND_CANCEL_NETWORK_ACTION -> stopNetRequest()
            FOREGROUND_CANCEL_LOCATION_ACTION -> stopLocationRequest()
            FOREGROUND_CANCEL_SEND_ACTION -> stopSendData()
        }
    }

    /**
     * You should call this method last
     */
    @CallSuper
    override fun onDestroy() {
        compositeDisposable.clear()
        if (runningNotifications.isNotEmpty()) {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        }
        stopSelf()
        super.onDestroy()
    }


    protected abstract fun getNotificationProvider(): SapNotificationProvider

    override fun onBind(intent: Intent): IBinder =
        SapServiceBinder()

    inner class SapServiceBinder : Binder() {
        val serviceName: String =
            name
    }
}
