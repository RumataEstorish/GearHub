package gearsoftware.gearhub.socialgear

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import gearsoftware.gearhub.R
import gearsoftware.gearhub.serviceprovider.AbstractSapService
import gearsoftware.gearhub.serviceprovider.ChannelGroup
import gearsoftware.gearhub.serviceprovider.ChannelInfo
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider
import toothpick.InjectConstructor

@InjectConstructor
class SocialGearNotificationProvider(
        private val context: Context,
        sapService: AbstractSapService
) : SapNotificationProvider(context, sapService) {

    companion object {
        private const val FOREGROUND_CONNECTION_CHANNEL_ID = "SOCIALGEAR_CONNECTION_CHANNEL_ID"
    }

    override fun getConnectionToWatchNotification(): NotificationCompat.Builder =
            super.getConnectionToWatchNotification()
                    .setSmallIcon(R.drawable.socialgear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.socialgear_foreground_icon_color))

    override fun getSendingDataToWatchNotification(): NotificationCompat.Builder =
            super.getSendingDataToWatchNotification()
                    .setSmallIcon(R.drawable.socialgear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.socialgear_foreground_icon_color))

    override fun getConnectionChannelInfo(): ChannelInfo =
            ChannelInfo(FOREGROUND_CONNECTION_CHANNEL_ID, context.getString(R.string.social_gear_foreground_channel_name), group = ChannelGroup("SocialGear", context.getString(R.string.socialgear)))

}