package gearsoftware.gearhub.servicesimpl.squaregear

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
class SquareGearNotificationProvider(
        private val context: Context,
        sapService: AbstractSapService
) : SapNotificationProvider(context, sapService) {

    companion object {
        private const val FOREGROUND_CONNECTION_CHANNEL_ID = "SQUAREGEAR_CONNECTION_CHANNEL_ID"
        private const val FOREGROUND_LOCATION_CHANNEL_ID = "SQUAREGEAR_LOCATION_CHANNEL_ID"
        private const val FOREGROUND_NET_CHANNEL_ID = "SQUAREGEAR_NET_CHANNEL_ID"
        private const val CHANNEL_GROUP_ID = "SquareGear"
    }

    override fun getConnectionToWatchNotification(): NotificationCompat.Builder =
            super.getConnectionToWatchNotification()
                    .setSmallIcon(R.drawable.squaregear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.squaregear_foreground_icon_color))

    override fun getSendingDataToWatchNotification(): NotificationCompat.Builder =
            super.getSendingDataToWatchNotification()
                    .setSmallIcon(R.drawable.squaregear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.squaregear_foreground_icon_color))

    override fun getLocationNotification(): NotificationCompat.Builder =
            super.getLocationNotification()
                    .setSmallIcon(R.drawable.squaregear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.squaregear_foreground_icon_color))

    override fun getNetworkNotification(): NotificationCompat.Builder =
            super.getNetworkNotification()
                    .setSmallIcon(R.drawable.squaregear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.squaregear_foreground_icon_color))

    override fun getOpenLinkNotification(address: String): NotificationCompat.Builder =
            super.getOpenLinkNotification(address)
                    .setSmallIcon(R.drawable.squaregear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.squaregear_foreground_icon_color))

    override fun getCommonChannelInfo(): ChannelInfo =
            super.getCommonChannelInfo().copy(group = ChannelGroup(CHANNEL_GROUP_ID, context.getString(R.string.squaregear)))

    override fun getConnectionChannelInfo(): ChannelInfo =
            getCommonChannelInfo().copy(id = FOREGROUND_CONNECTION_CHANNEL_ID, name = context.getString(R.string.squaregear_foreground_connection_channel_name))

    override fun getLocationChannelInfo(): ChannelInfo =
            getCommonChannelInfo().copy(id = FOREGROUND_LOCATION_CHANNEL_ID, name = context.getString(R.string.squaregear_foreground_location_channel_name))

    override fun getNetworkChannelInfo(): ChannelInfo =
            getCommonChannelInfo().copy(id = FOREGROUND_NET_CHANNEL_ID, name = context.getString(R.string.squaregear_foreground_network_channel_name))

}