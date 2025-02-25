package gearsoftware.sap.utils

import okhttp3.*

fun String.toMediaTypeOrNull() =
        MediaType.parse(this)

fun String.toRequestBody(mediaType: MediaType?) : RequestBody =
        RequestBody.create(mediaType, this)

val Response.bodyEx: ResponseBody?
    get() = this.body() ?: ResponseBody.create(null, "")

val Response.codeEx: Int
    get() = this.code()

val Response.headersEx: Headers
    get() = this.headers()