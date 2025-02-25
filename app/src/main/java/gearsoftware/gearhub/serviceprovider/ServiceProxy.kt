package gearsoftware.gearhub.serviceprovider

import android.graphics.drawable.Drawable
import gearsoftware.gearhub.services.SapServicePermissions

/**
 * Service
 */
open class ServiceProxy(
    val name: String,
    val description: String,
    val icon: Drawable,
    val className: String,
    val haveSettings: Boolean = false,
    val permissions: SapServicePermissions? = null,
    var checked: Boolean = false,
    val storeUrl: String
)