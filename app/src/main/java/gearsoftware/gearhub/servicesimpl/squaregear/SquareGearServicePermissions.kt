package gearsoftware.gearhub.servicesimpl.squaregear

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import gearsoftware.gearhub.R
import gearsoftware.gearhub.services.SapPermission
import gearsoftware.gearhub.services.SapServicePermissions

class SquareGearServicePermissions(
    private val context: Context
) : SapServicePermissions() {
    override val permissions: List<SapPermission>
        @SuppressLint("InlinedApi")
        get() = listOf(
            SapPermission(listOf(Manifest.permission.ACCESS_NETWORK_STATE), "", "", true),
            SapPermission(
                permissions = listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                title = context.getString(R.string.squaregear_background_location_title),
                description = context.getString(R.string.squaregear_background_location_description),
                required = false
            )
        )
}