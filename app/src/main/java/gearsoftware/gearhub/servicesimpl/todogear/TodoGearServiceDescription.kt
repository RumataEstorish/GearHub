package gearsoftware.gearhub.servicesimpl.todogear

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import gearsoftware.gearhub.R
import gearsoftware.gearhub.services.SapServiceDescription

class TodoGearServiceDescription(context: Context) : SapServiceDescription(context) {

    override val description: String by lazy {
        context.getString(R.string.todogear_description)
    }

    override val icon: Drawable by lazy {
        ContextCompat.getDrawable(context, R.mipmap.todogear_launcher_round)!!
    }
}