package gearsoftware.gearhub.servicesimpl.todogear

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
class TodoGearNotificationProvider(
        private val context: Context,
        sapService: AbstractSapService
) : SapNotificationProvider(context, sapService) {

    companion object {
        private const val FOREGROUND_CONNECTION_CHANNEL_ID = "TODOGEAR_FOREGROUND_CONNECTION_CHANNEL_ID"
        private const val FOREGROUND_NET_CHANNEL_ID = "TODOGEAR_FOREGROUND_NET_CHANNEL_ID"
        private const val CHANNEL_GROUP_ID = "TodoGear"
    }

    override fun getConnectionToWatchNotification(): NotificationCompat.Builder =
            super.getConnectionToWatchNotification()
                    .setSmallIcon(R.drawable.todogear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.todogear_foreground_icon_color))

    override fun getSendingDataToWatchNotification(): NotificationCompat.Builder =
            super.getSendingDataToWatchNotification()
                    .setSmallIcon(R.drawable.todogear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.todogear_foreground_icon_color))

    override fun getNetworkNotification(): NotificationCompat.Builder =
            super.getNetworkNotification()
                    .setSmallIcon(R.drawable.todogear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.todogear_foreground_icon_color))

    override fun getCommonChannelInfo(): ChannelInfo =
            super.getCommonChannelInfo().copy(group = ChannelGroup(CHANNEL_GROUP_ID, context.getString(R.string.todogear)))

    override fun getConnectionChannelInfo(): ChannelInfo =
            getCommonChannelInfo().copy(id = FOREGROUND_CONNECTION_CHANNEL_ID, name = context.getString(R.string.todogear_foreground_connection_channel_name))

    override fun getNetworkChannelInfo(): ChannelInfo =
            getCommonChannelInfo().copy(id = FOREGROUND_NET_CHANNEL_ID, name = context.getString(R.string.todogear_foreground_network_channel_name))
}