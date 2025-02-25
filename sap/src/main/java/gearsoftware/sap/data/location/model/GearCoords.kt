package gearsoftware.sap.data.location.model

import com.google.gson.annotations.SerializedName

internal data class GearCoords(

        @SerializedName("latitude")
        val latitude: Double = 0.0,

        @SerializedName("longitude")
        val longitude: Double = 0.0,

        @SerializedName("accuracy")
        val accuracy: Float = 0.0f,

        @SerializedName("altitude")
        val altitude: Double = 0.0,

        @SerializedName("altitudeAccuracy")
        val altitudeAccuracy: Float = 0.0f,

        @SerializedName("heading")
        val heading: Double =  0.0,

        @SerializedName("speed")
        val speed: Float = 0.0f

)