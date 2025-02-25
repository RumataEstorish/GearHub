package gearsoftware.gearhub.servicesimpl.taskgear

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
class TaskGearNotificationProvider(
        private val context: Context,
        sapService: AbstractSapService
) : SapNotificationProvider(context, sapService) {
    companion object {
        private const val FOREGROUND_CONNECTION_CHANNEL_ID = "TASKGEAR_FOREGROUND_CONNECTION_CHANNEL_ID"
        private const val FOREGROUND_NET_CHANNEL_ID = "TASKGEAR_FOREGROUND_NET_CHANNEL_ID"
        private const val CHANNEL_GROUP_ID = "TaskGear"
    }

    override fun getConnectionToWatchNotification(): NotificationCompat.Builder =
            super.getConnectionToWatchNotification()
                    .setSmallIcon(R.drawable.taskgear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.taskgear_foreground_icon_color))

    override fun getSendingDataToWatchNotification(): NotificationCompat.Builder =
            super.getSendingDataToWatchNotification()
                    .setSmallIcon(R.drawable.taskgear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.taskgear_foreground_icon_color))

    override fun getNetworkNotification(): NotificationCompat.Builder =
            super.getNetworkNotification()
                    .setSmallIcon(R.drawable.taskgear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.taskgear_foreground_icon_color))

    override fun getCommonChannelInfo(): ChannelInfo =
            super.getCommonChannelInfo().copy(group = ChannelGroup(CHANNEL_GROUP_ID, context.getString(R.string.taskgear)))


    override fun getConnectionChannelInfo(): ChannelInfo =
            getCommonChannelInfo().copy(id = FOREGROUND_CONNECTION_CHANNEL_ID, name = context.getString(R.string.taskgear_foreground_connection_channel_name))

    override fun getNetworkChannelInfo(): ChannelInfo =
            getCommonChannelInfo().copy(id = FOREGROUND_NET_CHANNEL_ID, name = context.getString(R.string.taskgear_foreground_network_channel_name))
}