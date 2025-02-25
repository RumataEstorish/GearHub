package gearsoftware.sap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.samsung.android.sdk.SsdkUnsupportedException
import com.samsung.android.sdk.accessory.SA
import com.samsung.android.sdk.accessory.SAAgent
import com.samsung.android.sdk.accessory.SAPeerAgent
import com.samsung.android.sdk.accessory.SASocket
import gearsoftware.sap.data.GearCommands
import gearsoftware.sap.data.GearStates
import gearsoftware.sap.data.ISchedulers
import gearsoftware.sap.data.gearhttp.model.AndroidHttpResponse
import gearsoftware.sap.data.gearhttp.model.HttpRequestData
import gearsoftware.sap.data.location.model.GearLocation
import gearsoftware.sap.data.model.WatchBitmap
import gearsoftware.sap.data.model.WatchText
import gearsoftware.sap.di.SapCommonModule
import gearsoftware.sap.di.SapModule
import gearsoftware.sap.di.Scopes
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject
import toothpick.ktp.extension.getInstance
import java.io.IOException

abstract class Sap(providerName: String) : SAAgent(providerName, SapConnection::class.java), ISap {

    companion object {
        private const val GET_LOCATION = "GET_LOCATION"

        internal const val OPEN_LINK = "OPEN_LINK"

        const val NO_CHANNEL = -1
        const val SERVICE_CHANNEL_ID = 98
        const val NETWORK_CHANNEL_ID = 99
        const val LOCATION_CHANNEL_ID = 97
        const val AUTH_NEEDED = "AUTH_NEEDED"
        const val REAUTH_NEEDED = "REAUTH_NEEDED"
        const val GET_LOCATION_CANCELLED = "GET_LOCATION_CANCELLED"
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var sapPresenter: SapPresenter
    private var disposable: Disposable? = null
    private val mBinder = LocalBinder()
    private val schedulers: ISchedulers by inject()

    @Volatile
    protected var peerAgent: SAPeerAgent? = null
        private set(value) {
            if (value == null) {
                field?.let { onGearStateSubject.onNext(GearStates.PeerAgentDisconnected) }
            }

            field = value
            value?.let {
                onGearStateSubject.onNext(GearStates.PeerAgentConnected)
            }

        }

    @Volatile
    private var socket: ISapConnection? = null
        private set(value) {
            value
                ?.let {
                    disposable = value.onReceive
                        .subscribeBy(
                            onNext = { onGearCommandSubject.onNext(GearCommands.Text(it)) },
                            onError = Timber::e
                        )
                } ?: disposable?.dispose()
            field = value
        }

    @Suppress("MemberVisibilityCanBePrivate")
    protected val maxDataSize
        get() = peerAgent?.maxAllowedDataSize ?: 0


    private val onGearCommandSubject: PublishSubject<GearCommands> = PublishSubject.create()
    override val onGearCommand: Observable<GearCommands> =
        onGearCommandSubject.hide()

    private val onGearStateSubject: PublishSubject<GearStates> = PublishSubject.create()
    override val onGearState: Observable<GearStates> =
        onGearStateSubject.hide()

    final override val connectedModel: String
        get() = peerAgent?.accessory?.productId ?: ""

    final override val isConnected: Boolean
        get() = socket?.isGearConnected ?: false && peerAgent != null


    @CallSuper
    override fun onCreate() {
        super.onCreate()
        val scope = KTP.openScope(Scopes.SAP)
            .installModules(SapModule(this), SapCommonModule())

        val mAccessory = SA()
        try {
            mAccessory.initialize(this)
        } catch (ignored: SsdkUnsupportedException) {

        }

        sapPresenter = SapPresenter(this, scope.getInstance())


        onGearStateSubject
            .filter { it is GearStates.Connected }
            .cast(GearStates.Connected::class.java)
            .doOnNext { sapPresenter.onConnectedChanged(it.isConnected) }
            .subscribeBy(onError = Timber::e)
            .addTo(compositeDisposable)
    }

    internal fun sendLocation(location: GearLocation) =
        sendText(WatchText(LOCATION_CHANNEL_ID, Gson().toJson(location)))

    internal fun sendLocationError(location: GearLocation) =
        sendText(WatchText(LOCATION_CHANNEL_ID, Gson().toJson(location)))

    internal fun sendNetResponse(response: AndroidHttpResponse) =
        sendText(WatchText(NETWORK_CHANNEL_ID, Gson().toJson(response)))

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    internal fun onCheckLocationPermission() {
        val backgroundLocation: Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PERMISSION_GRANTED
            } else {
                true
            }

        sapPresenter.onCheckLocationPermissionResult(
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
                    backgroundLocation
        )
    }

    internal fun onStartLocationRequest() {
        onGearStateSubject.onNext(GearStates.LocationRequestStarted)
    }

    internal fun onStopLocationRequest() {
        onGearStateSubject.onNext(GearStates.LocationRequestStopped)
    }

    internal fun onStartNetRequest() {
        onGearStateSubject.onNext(GearStates.NetworkRequestStarted)
    }

    internal fun onStopNetRequest() {
        onGearStateSubject.onNext(GearStates.NetworkRequestStopped)
    }

    override fun stopLocationRequest() {
        sapPresenter.onStopLocationRequest()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        sapPresenter.onDestroy()
        KTP.closeScope(Scopes.SAP)
        super.onDestroy()
    }

    override fun startConnectToWatch(endlessAttempts: Boolean) {
        sapPresenter.onStartConnectToWatch(endlessAttempts)
    }

    override fun stopConnectToWatch() {
        sapPresenter.onStopConnectToWatch()
    }

    override fun stopSendData() {
        sapPresenter.onStopSendingToWatch()
    }

    override fun stopNetRequest() {
        sapPresenter.onStopNetworkRequest()
    }

    override fun onServiceConnectionResponse(saPeerAgent: SAPeerAgent?, saSocket: SASocket?, result: Int) {
        when (result) {
            CONNECTION_ALREADY_EXIST,
            CONNECTION_DUPLICATE_REQUEST,
            CONNECTION_SUCCESS -> {
                saSocket?.let {
                    sapPresenter.onConnectedChanged(true)
                    socket = it as? SapConnection
                }
                peerAgent = saPeerAgent
                saPeerAgent?.let {
                    acceptServiceConnectionRequest(it)
                }
                onGearStateSubject.onNext(GearStates.Connected(isConnected))
            }
        }
    }

    override fun onError(peerAgent: SAPeerAgent?, errorMessage: String?, errorCode: Int) {
        super.onError(peerAgent, errorMessage, errorCode)
        sapPresenter.onError(errorMessage, errorCode)
    }

    override fun onServiceConnectionRequested(saPeerAgent: SAPeerAgent?) {
        onGearStateSubject.onNext(GearStates.Connected(false))
        sapPresenter.onStartConnectToWatch(false)
        saPeerAgent
            ?.let { acceptServiceConnectionRequest(saPeerAgent) }
            ?: run {
                peerAgent?.let { onGearStateSubject.onNext(GearStates.PeerAgentDisconnected) }
                peerAgent = null
            }
    }

    override fun onBind(intent: Intent): IBinder? = mBinder

    internal fun setSendingDataToWatch(sending: Boolean) {
        onGearStateSubject.onNext(
            if (sending) {
                GearStates.SendingStarted
            } else {
                GearStates.SendingStopped
            }
        )
    }

    internal fun setConnectingToWatch(connected: Boolean) {
        onGearStateSubject.onNext(
            if (connected) {
                GearStates.ConnectingStarted
            } else {
                GearStates.ConnectingStopped
            }
        )
    }

    internal fun sendText(channelId: Int, text: String) {
        socket?.send(channelId, text)
    }

    protected fun sendText(data: WatchText, endlessAttempts: Boolean = false) =
        sapPresenter.onSendText(data, endlessAttempts)

    protected fun sendBitmap(watchBitmap: WatchBitmap) =
        sapPresenter.onSendBitmap(watchBitmap)

    override fun onFindPeerAgentsResponse(peerAgents: Array<out SAPeerAgent>?, result: Int) {
        peerAgents?.let {
            peerAgent = it.first()
            Timber.d("onFindPeerAgentsResponse: $result")
            acceptServiceConnectionRequest(it.first())
            requestServiceConnection(it.first())
        }
    }

    override fun onPeerAgentsUpdated(peerAgents: Array<out SAPeerAgent>?, reason: Int) {
        peerAgent = peerAgents?.first()
    }

    internal fun forwardServiceChannelRequest(res: String) {
        onGearCommandSubject.onNext(GearCommands.Text(WatchText(SERVICE_CHANNEL_ID, res)))
    }

    internal fun onOpenWebPage(address: String) {
        onGearCommandSubject.onNext(GearCommands.OpenWebPage(address))
    }

    internal fun tryConnectWatch() {
        peerAgent
            ?.let(::requestServiceConnection)
            ?: findPeerAgents()
    }

    inner class LocalBinder : Binder() {
        val service: Sap
            get() = this@Sap
    }

    private inner class SapConnection : SASocket(""), ISapConnection {

        private val compositeDisposable = CompositeDisposable()

        private val onServiceConnectionLostSubject: PublishSubject<Int> = PublishSubject.create()
        override val onServiceConnectionLost: Observable<Int> = onServiceConnectionLostSubject.hide()

        private val onReceiveSubject: PublishSubject<WatchText> = PublishSubject.create()
        override val onReceive: Observable<WatchText> = onReceiveSubject.hide()

        override val isGearConnected: Boolean
            get() = isConnected

        override fun onError(channelId: Int, errorString: String, errorNumber: Int) {
            Timber.e(errorString)
        }

        override fun send(channelId: Int, data: String) {
            Completable.fromAction {
                if (isConnected) {
                    try {
                        send(channelId, data.toByteArray())
                    } catch (ignored: IOException) {
                    }
                }
            }
                .observeOn(schedulers.io)
                .doFinally(sapPresenter::onStopSendingToWatch)
                .subscribeBy(onError = Timber::e)
                .addTo(compositeDisposable)
        }

        override fun onReceive(channelId: Int, data: ByteArray) {
            val res = String(data)
            val gson = Gson()

            when (channelId) {
                LOCATION_CHANNEL_ID -> {
                    when (res) {
                        GET_LOCATION -> sapPresenter.onLocationRequest()
                    }
                }
                SERVICE_CHANNEL_ID -> sapPresenter.onServiceChannelRequest(res)
                NETWORK_CHANNEL_ID -> sapPresenter.onNetworkRequest(gson.fromJson(res, HttpRequestData::class.java))
                else -> onReceiveSubject.onNext(WatchText(channelId, res))
            }
        }

        override fun close() {
            super.close()
            compositeDisposable.clear()
        }

        override fun onServiceConnectionLost(p0: Int) {
            sapPresenter.onConnectedChanged(false)
            peerAgent = null
            socket = null
        }
    }
}