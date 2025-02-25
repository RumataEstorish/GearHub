package gearsoftware.sap.data.location

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import gearsoftware.sap.utils.safeOnError
import gearsoftware.sap.utils.safeOnSuccess
import io.reactivex.rxjava3.core.Single
import toothpick.InjectConstructor

@InjectConstructor
internal class LocationSource(
        private val context: Context
) {
    private val locationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLocationAvailability(): Single<Boolean> =
            Single.create { emitter ->
                locationProviderClient.locationAvailability.addOnSuccessListener { emitter.safeOnSuccess(it.isLocationAvailable) }
                locationProviderClient.locationAvailability.addOnFailureListener { emitter.safeOnError(it) }
            }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLastLocation(): Single<Location> =
            Single.create { emitter ->
                locationProviderClient.lastLocation.addOnSuccessListener { emitter.safeOnSuccess(it) }
                locationProviderClient.lastLocation.addOnFailureListener { emitter.safeOnError(it) }
            }


}