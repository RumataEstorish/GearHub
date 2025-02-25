package gearsoftware.gearhub.servicesimpl.transgear

import android.Manifest
import gearsoftware.gearhub.services.SapPermission
import gearsoftware.gearhub.services.SapServicePermissions

class TransGearServicePermissions : SapServicePermissions() {
    override val permissions: List<SapPermission>
        get() = listOf(
            SapPermission(listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), "", "", true)
        )
}