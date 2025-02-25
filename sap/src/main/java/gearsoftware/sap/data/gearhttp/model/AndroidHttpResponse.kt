package gearsoftware.sap.data.gearhttp.model

import com.google.gson.annotations.SerializedName

/**
 * Http response
 */
internal data class AndroidHttpResponse(
        @SerializedName("status")
        val status: Int = 0,

        @SerializedName("readyState")
        val readyState: Int = 0,

        @SerializedName("responseText")
        val responseText: String? = null,

        @SerializedName("imagePath")
        val imagePath: String = "",

        @SerializedName("type")
        val type: String = "GET",

        @SerializedName("requestAddress")
        val requestAddress: String? = null,

        @SerializedName("fileName")
        val fileName: String? = null
) {
    companion object {
        fun getNoInternetResponse(requestAddress: String?, type: String): AndroidHttpResponse =
                AndroidHttpResponse(
                        status = 0,
                        readyState = 4,
                        requestAddress = requestAddress,
                        responseText = "",
                        type = type
                )
    }
}
