package gearsoftware.sap.data.location.model

import com.google.gson.annotations.SerializedName

internal data class GearLocation(

        @SerializedName("coords")
        val coords: GearCoords? = null,

        @SerializedName("timestamp")
        val time: Long = 0,

        @SerializedName("code")
        val code: Int = 0,

        @SerializedName("message")
        val message: String = ""
)