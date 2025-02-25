package gearsoftware.sap.data.gearhttp.model

import com.google.gson.annotations.SerializedName

/**
 * Web request header
 */
internal data class RequestHeader(
        @SerializedName("name")
        val name: String = "",
        @SerializedName("value")
        var value: String = ""
)
