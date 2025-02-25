package gearsoftware.gearhub.serviceprovider

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import gearsoftware.gearhub.services.SapServicePermissions

/**
 * Web service description
 */
open class ServiceWebProxy(
    name: String,
    description: String,
    icon: Drawable,
    className: String,
    haveSettings: Boolean = false,
    permissions: SapServicePermissions? = null,
    checked: Boolean = false,
    var isLoggedIn: Boolean = false,
    var userName: String = "",
    storeUrl: String
) : ServiceProxy(name, description, icon, className, haveSettings, permissions, checked, storeUrl)