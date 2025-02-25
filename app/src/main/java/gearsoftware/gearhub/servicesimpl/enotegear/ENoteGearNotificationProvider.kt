package gearsoftware.gearhub.servicesimpl.enotegear

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import gearsoftware.gearhub.R
import gearsoftware.gearhub.servicesimpl.enotegear.model.SyncStatus
import gearsoftware.gearhub.serviceprovider.AbstractSapService
import gearsoftware.gearhub.serviceprovider.ChannelGroup
import gearsoftware.gearhub.serviceprovider.ChannelInfo
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider
import toothpick.InjectConstructor

@InjectConstructor
class ENoteGearNotificationProvider(
        private val context: Context,
        sapService: AbstractSapService
) : SapNotificationProvider(context, sapService) {

    companion object {
        private const val FOREGROUND_SYNC_CHANNEL_ID = "ENOTEGEAR_SYNC_CHANNEL_ID"
    }

    override fun getConnectionToWatchNotification(): NotificationCompat.Builder =
            super.getConnectionToWatchNotification()
                    .setSmallIcon(R.drawable.enotegear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.enotegear_foreground_icon_color))

    override fun getSendingDataToWatchNotification(): NotificationCompat.Builder =
            super.getSendingDataToWatchNotification()
                    .setSmallIcon(R.drawable.enotegear_notification_ic)
                    .setColor(ContextCompat.getColor(context, R.color.enotegear_foreground_icon_color))

    override fun getCommonChannelInfo(): ChannelInfo =
            ChannelInfo(FOREGROUND_SYNC_CHANNEL_ID, context.getString(R.string.enotegear_foreground_channel_name), group = ChannelGroup("ENoteGear", context.getString(R.string.enotegear)))

    fun getSyncNotification(status: SyncStatus): NotificationCompat.Builder =
            super.getCommonNotification()
                    .setColor(ContextCompat.getColor(context, R.color.enotegear_foreground_icon_color))
                    .setSmallIcon(R.drawable.enotegear_notification_ic)
                    .setContentTitle(context.getString(R.string.enotegear_notification_title))
                    .setContentText(
                            when (status) {
                                SyncStatus.IDLE -> context.getString(R.string.enotegear_exchange_data)
                                SyncStatus.NOTEBOOKS -> context.getString(R.string.enotegear_notebooks)
                                SyncStatus.NOTES -> context.getString(R.string.enotegear_notes)
                                SyncStatus.TAGS -> context.getString(R.string.enotegear_tags)
                            }
                    )
}