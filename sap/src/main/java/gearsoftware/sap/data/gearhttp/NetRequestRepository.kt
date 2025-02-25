package gearsoftware.sap.data.gearhttp

import gearsoftware.sap.data.ISchedulers
import gearsoftware.sap.data.gearhttp.model.AndroidHttpResponse
import gearsoftware.sap.data.gearhttp.model.HttpRequestData
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import toothpick.InjectConstructor
import java.util.concurrent.TimeUnit

@InjectConstructor
internal class NetRequestRepository(
        private val netRequestSource: NetRequestSource,
        private val requestMapper: RequestMapper,
        private val schedulers: ISchedulers
) {

    companion object {
        private const val RETRIES_LIMIT = 5
        private const val RETRY_INTERVAL = 5000L
    }

    fun request(requestData: HttpRequestData): Single<AndroidHttpResponse> =
            netRequestSource.execute(RequestMapper.toAndroid(requestData))
                    .retryWhen { err ->
                        err.zipWith(
                                Flowable.range(1, RETRIES_LIMIT),
                                { error: Throwable, retryCount: Int ->
                                    if (retryCount > RETRIES_LIMIT) {
                                        throw error
                                    } else {
                                        retryCount
                                    }
                                })
                                .flatMap { Flowable.timer(RETRY_INTERVAL, TimeUnit.MILLISECONDS) }
                    }
                    .map { requestMapper.toGear(it, requestData.type, netRequestSource.isOnline()) }
                    .onErrorReturnItem(AndroidHttpResponse.getNoInternetResponse(requestData.address, requestData.type))
                    .subscribeOn(schedulers.io)
}