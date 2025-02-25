package gearsoftware.sap

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import gearsoftware.sap.Sap.Companion.GET_LOCATION_CANCELLED
import gearsoftware.sap.Sap.Companion.LOCATION_CHANNEL_ID
import gearsoftware.sap.Sap.Companion.NO_CHANNEL
import gearsoftware.sap.data.gearhttp.model.AndroidHttpResponse
import gearsoftware.sap.data.gearhttp.model.HttpRequestData
import gearsoftware.sap.data.model.ServiceRequest
import gearsoftware.sap.data.model.WatchBitmap
import gearsoftware.sap.data.model.WatchText
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.InjectConstructor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@InjectConstructor
internal class SapPresenter(
    private val view: Sap,
    private val sapInteractor: SapInteractor
) {

    companion object {
        private const val REFRESH_DELAY = 5000L
        private const val REFRESH_CONNECT_TIMER_INTERVAL = 10000L
        private const val MAX_CONNECT_ATTEMPTS = 5
    }

    private val compositeDisposable = CompositeDisposable()
    private val netRequestDisposable = CompositeDisposable()
    private val locationRequestDisposable = CompositeDisposable()
    private val connectDisposable = AtomicReference<Disposable?>()

    private val sendQueue: MutableSet<WatchText> = mutableSetOf()
    private var lastRequest: HttpRequestData? = null

    @Volatile
    private var isConnected: Boolean = false

    fun onConnectedChanged(connected: Boolean) {
        isConnected = connected
        if (connected) {
            connectDisposable.get()?.dispose()

            if (sendQueue.isNotEmpty()) {
                sendQueue.forEach { view.sendText(it.channelId, it.data) }
                sendQueue.clear()
            }
        }
    }

    fun onNetworkRequest(request: HttpRequestData) {
        view.onStartNetRequest()
        lastRequest = request
        sapInteractor.networkRequest(request)
            .doOnSuccess(view::sendNetResponse)
            .subscribeBy(
                onSuccess = { view.onStopNetRequest() },
                onError = {
                    Timber.e(it)
                    view.onStopNetRequest()
                })
            .addTo(netRequestDisposable)
    }

    @SuppressLint("MissingPermission")
    fun onLocationRequest() {
        view.onCheckLocationPermission()
    }

    fun onCheckLocationPermissionResult(havePermission: Boolean) {
        view.onStartLocationRequest()

        sapInteractor.getLocation(havePermission)
            .doOnSuccess(view::sendLocation)
            .subscribeBy(
                onSuccess = { view.onStopLocationRequest() },
                onError = Timber::e
            )
            .addTo(locationRequestDisposable)
    }

    fun onDestroy() {
        locationRequestDisposable.dispose()
        compositeDisposable.dispose()
        netRequestDisposable.dispose()
    }

    fun onServiceChannelRequest(res: String) {
        val req: ServiceRequest
        try {
            req = Gson().fromJson(res, ServiceRequest::class.java)
        } catch (e: JsonSyntaxException) {
            view.forwardServiceChannelRequest(res)
            return
        }

        when (req.name) {
            Sap.OPEN_LINK -> req.value?.let {
                if (it.isNotBlank()) {
                    view.onOpenWebPage(it)
                }
            }
        }
    }

    fun onSendText(data: WatchText, endlessAttempts: Boolean = false) {
        if (data.channelId == NO_CHANNEL || data.data.isBlank()) {
            return
        }

        view.setSendingDataToWatch(true)
        Completable.fromCallable {
            if (isConnected) {
                view.sendText(data.channelId, data.data)
            } else {
                sendQueue.add(data)
                view.startConnectToWatch(endlessAttempts)
                view.setSendingDataToWatch(false)
            }
        }
            .subscribeBy(onError = Timber::e)
            .addTo(compositeDisposable)
    }

    fun onSendBitmap(data: WatchBitmap) {
        view.setSendingDataToWatch(true)
        sapInteractor.convertBitmapToText(data.bitmap)
            .subscribeBy(
                onSuccess = { view.sendText(data.channelId, it) },
                onError = Timber::e
            )
            .addTo(compositeDisposable)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onError(errorMessage: String?, errorCode: Int) {
        Timber.e(errorMessage)
    }

    fun onStopSendingToWatch() {
        view.setSendingDataToWatch(false)
    }

    fun onStartConnectToWatch(endlessAttempts: Boolean) {
        connectDisposable.get()?.let {
            if (!it.isDisposed) {
                return@onStartConnectToWatch
            }
        }

        val retryTimes = AtomicInteger(1)
        view.setConnectingToWatch(true)

        connectDisposable.set(Observable.interval(REFRESH_DELAY, REFRESH_CONNECT_TIMER_INTERVAL, TimeUnit.MILLISECONDS)
            .takeUntil {
                if (!endlessAttempts && retryTimes.get() == MAX_CONNECT_ATTEMPTS) {
                    return@takeUntil true
                }
                isConnected
            }
            .doOnDispose {
                view.setConnectingToWatch(false)
            }
            .subscribeBy(
                onNext = {
                    retryTimes.incrementAndGet()
                    view.tryConnectWatch()
                },
                onError = Timber::e,
                onComplete = { view.setConnectingToWatch(false) })
        )
    }

    fun onStopConnectToWatch() {
        connectDisposable.get()?.dispose()
        view.setConnectingToWatch(false)
    }

    fun onStopLocationRequest() {
        locationRequestDisposable.clear()
        onSendText(WatchText(LOCATION_CHANNEL_ID, GET_LOCATION_CANCELLED))
        view.onStopLocationRequest()
    }

    fun onStopNetworkRequest() {
        netRequestDisposable.clear()
        view.sendNetResponse(
            AndroidHttpResponse.getNoInternetResponse(
                lastRequest?.address, lastRequest?.type
                    ?: "GET"
            )
        )
        view.onStopNetRequest()
    }

}