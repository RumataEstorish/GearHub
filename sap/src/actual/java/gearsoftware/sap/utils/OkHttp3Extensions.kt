package gearsoftware.sap.utils

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

fun String.toMediaTypeOrNull() =
    toMediaTypeOrNull()

fun String.toRequestBody(mediaType: MediaType?) : RequestBody =
    toRequestBody(mediaType)

val Response.bodyEx: ResponseBody?
    get() = body

val Response.codeEx: Int
    get() = code

val Response.headersEx: Headers
    get() = headers