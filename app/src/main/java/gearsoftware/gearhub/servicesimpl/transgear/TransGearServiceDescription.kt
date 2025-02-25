package gearsoftware.gearhub.servicesimpl.transgear

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import gearsoftware.gearhub.R
import gearsoftware.gearhub.services.SapServiceDescription

class TransGearServiceDescription(context: Context) : SapServiceDescription(context) {

    override val description: String by lazy {
        context.getString(R.string.transgear_description)
    }

    override val icon: Drawable by lazy {
        ContextCompat.getDrawable(context, R.mipmap.transgear_launcher_round)!!
    }
}