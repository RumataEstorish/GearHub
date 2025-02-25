package gearsoftware.sap.data.location

import android.Manifest
import androidx.annotation.RequiresPermission
import gearsoftware.sap.data.ISchedulers
import gearsoftware.sap.data.location.model.GearLocation
import io.reactivex.rxjava3.core.Single
import toothpick.InjectConstructor

@InjectConstructor
internal class LocationRepository(
        private val locationSource: LocationSource,
        private val locationMapper: LocationMapper,
        private val schedulers: ISchedulers
) {

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLocationAvailability(): Single<Boolean> =
            locationSource.getLocationAvailability()
                    .subscribeOn(schedulers.io)

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLastLocation(): Single<GearLocation> =
            locationSource.getLastLocation()
                    .map(locationMapper::toGear)
                    .onErrorReturn(locationMapper::toGear)
                    .subscribeOn(schedulers.io)
}