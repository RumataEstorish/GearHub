package gearsoftware.gearhub.socialgear

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import gearsoftware.gearhub.R
import gearsoftware.gearhub.services.SapServiceDescription

class SocialGearServiceDescription(context: Context) : SapServiceDescription(context) {

    override val description: String by lazy {
        context.getString(R.string.socialgear_description)
    }

    override val icon: Drawable by lazy {
        ContextCompat.getDrawable(context, R.mipmap.socialgear_launcher_round)!!
    }
}