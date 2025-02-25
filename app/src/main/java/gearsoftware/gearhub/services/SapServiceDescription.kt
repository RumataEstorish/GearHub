package gearsoftware.gearhub.services

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.Keep

@Keep
abstract class SapServiceDescription(
    val context: Context
) {
    abstract val description: String
    abstract val icon: Drawable
}