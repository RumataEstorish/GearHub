package gearsoftware.sap.data.gearhttp

import gearsoftware.sap.data.gearhttp.model.AndroidHttpResponse
import gearsoftware.sap.data.gearhttp.model.HttpRequestData
import gearsoftware.sap.utils.*
import okhttp3.Request
import okhttp3.Response


internal object RequestMapper {
    fun toAndroid(data: HttpRequestData): Request {

        val builder = Request.Builder()
                .url(data.address)

        data.headers.forEach {
            builder.addHeader(it.name, it.value)
        }

        when (data.type) {
            "POST" -> {
                builder.post(data.headers
                        .firstOrNull { it.name.lowercase() == "content-type" }
                        ?.let {
                            data.args!!.toRequestBody(it.value.toMediaTypeOrNull())
                        } ?: "".toRequestBody(null)
                )
            }
        }
        return builder.build()
    }



    fun toGear(response: Response, type: String, isOnline: Boolean): AndroidHttpResponse =
            AndroidHttpResponse(
                    status = if (isOnline) {
                        response.codeEx
                    } else {
                        0
                    },
                    readyState = 4,
                    requestAddress = response.headersEx["Location"],
                    responseText = response.bodyEx?.string(),
                    type = type
            )
}