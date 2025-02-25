package gearsoftware.sap.data.model

import com.google.gson.annotations.SerializedName

/**
 * Service request from watch
 */
internal data class ServiceRequest(
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("value")
        var value: String? = null
)
