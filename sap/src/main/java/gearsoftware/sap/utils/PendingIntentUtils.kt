package gearsoftware.sap.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object PendingIntentUtils {
    fun getImmutableUpdateFlag(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT xor PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

    fun getService(context: Context, requestCode: Int, intent: Intent): PendingIntent =
        PendingIntent.getService(
            context,
            requestCode,
            intent,
            getImmutableUpdateFlag()
        )


    fun getActivity(context: Context, requestCode: Int, intent: Intent): PendingIntent =
        PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            getImmutableUpdateFlag()
        )

    fun getBroadcast(context: Context, requestCode: Int, intent: Intent): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            getImmutableUpdateFlag()
        )
}

