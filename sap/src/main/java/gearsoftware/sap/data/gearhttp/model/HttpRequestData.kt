package gearsoftware.sap.data.gearhttp.model

import com.google.gson.annotations.SerializedName

/**
 * Request data
 */
internal data class HttpRequestData(
        @SerializedName("address")
        val address: String = "",
        @SerializedName("type")
        val type: String = "",
        @SerializedName("args")
        val args: String? = null,
        @SerializedName("headers")
        val headers: List<RequestHeader> = emptyList()
)