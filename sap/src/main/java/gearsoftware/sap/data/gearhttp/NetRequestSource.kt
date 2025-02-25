package gearsoftware.sap.data.gearhttp

import gearsoftware.sap.utils.safeOnError
import gearsoftware.sap.utils.safeOnSuccess
import io.reactivex.rxjava3.core.Single
import okhttp3.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

internal class NetRequestSource(
        private val client: OkHttpClient
) {
    companion object {
        private const val SOCKET_TIMEOUT = 1500
        private const val SOCKET_IP = "8.8.8.8"
        private const val SOCKET_PORT = 53
        private const val INTERNET_STATUS_UPDATE_INTERVAL = 5000L
    }

    fun execute(request: Request): Single<Response> =
            Single.create { emitter ->
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        emitter.safeOnError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        emitter.safeOnSuccess(response)
                    }
                })
            }

    fun isOnline(): Boolean {
        try {
            Socket().use { sock ->
                sock.connect(InetSocketAddress(SOCKET_IP, SOCKET_PORT), SOCKET_TIMEOUT)
                return true
            }
        } catch (e: IOException) {
            return false
        }
    }
}