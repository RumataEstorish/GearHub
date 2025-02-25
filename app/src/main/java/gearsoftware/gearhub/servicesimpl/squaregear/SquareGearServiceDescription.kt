package gearsoftware.gearhub.servicesimpl.squaregear

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import gearsoftware.gearhub.R
import gearsoftware.gearhub.services.SapServiceDescription

class SquareGearServiceDescription(context: Context) : SapServiceDescription(context) {
    override val description: String by lazy {
        context.getString(R.string.squaregear_description)
    }

    override val icon: Drawable by lazy {
        ContextCompat.getDrawable(context, R.mipmap.squaregear_launcher_round)!!
    }
}