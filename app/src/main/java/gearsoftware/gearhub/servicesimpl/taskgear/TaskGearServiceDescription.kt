package gearsoftware.gearhub.servicesimpl.taskgear

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import gearsoftware.gearhub.R
import gearsoftware.gearhub.services.SapServiceDescription

class TaskGearServiceDescription(context: Context) : SapServiceDescription(context) {

    override val description: String by lazy {
        context.getString(R.string.taskgear_description)
    }

    override val icon: Drawable by lazy {
        ContextCompat.getDrawable(context, R.mipmap.taskgear_launcher_round)!!
    }
}