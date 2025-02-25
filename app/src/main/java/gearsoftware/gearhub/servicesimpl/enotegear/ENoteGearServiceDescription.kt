package gearsoftware.gearhub.servicesimpl.enotegear

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import gearsoftware.gearhub.R
import gearsoftware.gearhub.services.SapServiceDescription

class ENoteGearServiceDescription(context: Context) : SapServiceDescription(context) {
    override val description: String by lazy {
        context.getString(R.string.enotegear_description)
    }

    override val icon: Drawable by lazy {
        ContextCompat.getDrawable(context, R.mipmap.enotegear_launcher_round)!!
    }
}