package gearsoftware.sap.data.location

import android.location.Location
import gearsoftware.sap.data.location.model.GearCoords
import gearsoftware.sap.data.location.model.GearLocation

internal object LocationMapper {

    private const val POSITION_UNAVAILABLE_CODE = 2
    private const val PERMISSION_DENIED_CODE = 1

    fun toGear(location: Location): GearLocation =
            GearLocation(
                    time = location.time,
                    coords = GearCoords(
                            longitude = location.longitude,
                            latitude = location.latitude,
                            accuracy = location.accuracy,
                            altitude = location.altitude,
                            altitudeAccuracy = location.accuracy,
                            speed = location.speed
                    )
            )

    fun toGear(exception: Throwable): GearLocation =
            GearLocation(message = exception.localizedMessage ?: exception.message ?: exception.toString())

    fun locationPermissionDenied(): GearLocation =
            GearLocation(code = PERMISSION_DENIED_CODE)

    fun locationUnavailableToGear(): GearLocation =
            GearLocation(code = POSITION_UNAVAILABLE_CODE)
}