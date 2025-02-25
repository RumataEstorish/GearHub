package gearsoftware.sap

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Base64
import gearsoftware.sap.data.ISchedulers
import gearsoftware.sap.data.gearhttp.NetRequestRepository
import gearsoftware.sap.data.gearhttp.model.AndroidHttpResponse
import gearsoftware.sap.data.gearhttp.model.HttpRequestData
import gearsoftware.sap.data.location.LocationMapper
import gearsoftware.sap.data.location.LocationRepository
import gearsoftware.sap.data.location.model.GearLocation
import io.reactivex.rxjava3.core.Single
import java.io.ByteArrayOutputStream

internal class SapInteractor(
        private val netRepository: NetRequestRepository,
        private val locationRepository: LocationRepository,
        private val schedulers: ISchedulers,
        private val locationMapper: LocationMapper
) {

    fun networkRequest(request: HttpRequestData): Single<AndroidHttpResponse> =
            netRepository.request(request)

    @SuppressLint("MissingPermission")
    fun getLocation(permissionGranted: Boolean): Single<GearLocation> {
        if (!permissionGranted) {
            return Single.just(locationMapper.locationPermissionDenied())
        }

        return locationRepository.getLocationAvailability()
                .flatMap {
                    if (it) {
                        locationRepository.getLastLocation()
                    } else {
                        Single.just(locationMapper.locationUnavailableToGear())
                    }
                }
    }

    fun convertBitmapToText(img: Bitmap): Single<String> =
            Single.fromCallable {
                ByteArrayOutputStream().apply {
                    img.compress(Bitmap.CompressFormat.PNG, 0, this)
                }
            }
                    .map { Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP) }
                    .subscribeOn(schedulers.io)
}
