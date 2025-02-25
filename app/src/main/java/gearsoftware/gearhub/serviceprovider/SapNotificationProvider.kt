package gearsoftware.gearhub.serviceprovider

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import gearsoftware.gearhub.R
import gearsoftware.gearhub.serviceprovider.AbstractSapService.Companion.FOREGROUND_CHANNEL_ID
import gearsoftware.gearhub.serviceprovider.AbstractSapService.Companion.SERVICE_NAME_EXTRA
import gearsoftware.gearhub.view.main.MainActivity
import gearsoftware.sap.utils.PendingIntentUtils

abstract class SapNotificationProvider(
    private val context: Context,
    private val sapService: AbstractSapService
) {

    companion object {

        private const val STACK_REQUEST_CODE = 88725

        const val FOREGROUND_CANCEL_CONNECT_REQUEST_CODE = 23456
        const val FOREGROUND_CANCEL_NETWORK_REQUEST_CODE = 23457
        const val FOREGROUND_CANCEL_LOCATION_REQUEST_CODE = 23458
        const val FOREGROUND_OPEN_LINK_REQUEST_CODE = 23459
        const val FOREGROUND_CANCEL_SEND_REQUEST_CODE = 23460

        const val OPEN_LINK_ACTION = "gearsoftware.gearhub.OPEN_LINK_ACTION"
        const val NOTIFICATION_EXTRA = "gearsoftware.gearhub.NOTIFICATION_EXTRA"

        const val FOREGROUND_CANCEL_CONNECT_ACTION = "gearsoftware.gearhub.CANCEL_CONNECT_ACTION"
        const val FOREGROUND_CANCEL_SEND_ACTION = "gearsoftware.gearhub.CANCEL_SEND_ACTION"
        const val FOREGROUND_CANCEL_NETWORK_ACTION = "gearsoftware.gearhub.CANCEL_NETWORK_ACTION"
        const val FOREGROUND_CANCEL_LOCATION_ACTION = "gearsoftware.gearhub.CANCEL_LOCATION_ACTION"
    }

    private fun getRequestCode(id: Int): Int =
        id + sapService.name.hashCode()

    @Suppress("MemberVisibilityCanBePrivate")
    protected val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }

    fun cancelAll() {
        notificationManager.cancelAll()
    }

    private fun createNotification(
        channelInfo: ChannelInfo,
        @DrawableRes iconId: Int = R.drawable.notification_icon,
        @ColorRes iconColor: Int = R.color.icon_color,
        contentTitle: String = context.getString(R.string.foreground_title),
        contentText: String = context.getString(R.string.foreground_content)
    ): NotificationCompat.Builder {

        val intent = Intent(context, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(intent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel: NotificationChannel? = notificationManager.getNotificationChannel(channelInfo.id)
            if (channel == null) {

                @SuppressLint("WrongConstant")
                channel = NotificationChannel(channelInfo.id, channelInfo.name, channelInfo.importance)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    channelInfo.group?.let {
                        if (notificationManager.getNotificationChannelGroup(it.id) == null) {
                            notificationManager.createNotificationChannelGroup(NotificationChannelGroup(it.id, it.name))
                        }
                        channel.group = channelInfo.group.id
                    }
                }
                channel.setShowBadge(channelInfo.showBadge)
                channel.description = channelInfo.description

                notificationManager.createNotificationChannel(channel)
            }
        }

        return NotificationCompat.Builder(context, channelInfo.id)
            .setSmallIcon(iconId)
            .setColor(ContextCompat.getColor(context, iconColor))
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentIntent(stackBuilder.getPendingIntent(STACK_REQUEST_CODE, PendingIntentUtils.getImmutableUpdateFlag()))
    }

    @SuppressLint("InlinedApi")
    open fun getOpenLinkChannelInfo(): ChannelInfo =
        ChannelInfo(
            AbstractSapService.FOREGROUND_OPEN_LINK_CHANNEL_ID,
            context.getString(R.string.foreground_service_open_link_description),
            importance = NotificationManager.IMPORTANCE_HIGH,
            showBadge = true
        )

    open fun getConnectionChannelInfo(): ChannelInfo =
        ChannelInfo(AbstractSapService.FOREGROUND_CONNECTION_CHANNEL_ID, context.getString(R.string.foreground_service_connection_description))

    open fun getSendingDataToWatchChannelInfo(): ChannelInfo =
        ChannelInfo(AbstractSapService.FOREGROUND_SEND_DATA_TO_WATCH_CHANNEL_ID, context.getString(R.string.foreground_service_send_data_description))

    open fun getNetworkChannelInfo(): ChannelInfo =
        ChannelInfo(AbstractSapService.FOREGROUND_NET_CHANNEL_ID, context.getString(R.string.foreground_service_net_description))

    open fun getLocationChannelInfo(): ChannelInfo =
        ChannelInfo(AbstractSapService.FOREGROUND_LOCATION_CHANNEL_ID, context.getString(R.string.foreground_service_location_description))

    open fun getSendingDataCancelAction(serviceName: String): NotificationCompat.Action =
        NotificationCompat.Action(
            R.drawable.empty,
            context.getString(android.R.string.cancel),
            PendingIntentUtils.getService(
                context,
                getRequestCode(FOREGROUND_CANCEL_SEND_REQUEST_CODE),
                Intent(context, NotificationsSignalService::class.java).apply {
                    action = FOREGROUND_CANCEL_SEND_ACTION
                    putExtra(SERVICE_NAME_EXTRA, serviceName)
                }
            )
        )

    open fun getConnectionCancelAction(serviceName: String): NotificationCompat.Action =
        NotificationCompat.Action(
            R.drawable.empty,
            context.getString(android.R.string.cancel),
            PendingIntentUtils.getService(
                context,
                getRequestCode(FOREGROUND_CANCEL_CONNECT_REQUEST_CODE),
                Intent(context, NotificationsSignalService::class.java).apply {
                    action = FOREGROUND_CANCEL_CONNECT_ACTION
                    putExtra(SERVICE_NAME_EXTRA, serviceName)
                }
            )
        )

    open fun getLocationCancelAction(serviceName: String): NotificationCompat.Action =
        NotificationCompat.Action(
            R.drawable.empty,
            context.getString(android.R.string.cancel),
            PendingIntentUtils.getService(
                context,
                getRequestCode(FOREGROUND_CANCEL_LOCATION_REQUEST_CODE),
                Intent(context, NotificationsSignalService::class.java).apply {
                    action = FOREGROUND_CANCEL_LOCATION_ACTION
                    putExtra(SERVICE_NAME_EXTRA, serviceName)
                }
            )
        )

    open fun getNetCancelAction(serviceName: String): NotificationCompat.Action =
        NotificationCompat.Action(
            R.drawable.empty,
            context.getString(android.R.string.cancel),
            PendingIntentUtils.getService(
                context,
                getRequestCode(FOREGROUND_CANCEL_NETWORK_REQUEST_CODE),
                Intent(context, NotificationsSignalService::class.java).apply {
                    action = FOREGROUND_CANCEL_NETWORK_ACTION
                    putExtra(SERVICE_NAME_EXTRA, serviceName)
                }
            )
        )


    open fun getLocationNotification(): NotificationCompat.Builder =
        createNotification(
            channelInfo = getLocationChannelInfo(),
            contentTitle = context.getString(R.string.foreground_location_title),
            contentText = context.getString(R.string.foreground_location_content)
        ).apply { addAction(getLocationCancelAction(sapService.name)) }

    open fun getNetworkNotification(): NotificationCompat.Builder =
        createNotification(
            channelInfo = getNetworkChannelInfo(),
            contentTitle = context.getString(R.string.foreground_net_title),
            contentText = context.getString(R.string.foreground_net_content)
        ).apply { addAction(getNetCancelAction(sapService.name)) }

    open fun getConnectionToWatchNotification(): NotificationCompat.Builder =
        createNotification(
            channelInfo = getConnectionChannelInfo(),
            contentTitle = context.getString(R.string.foreground_connection_title),
            contentText = context.getString(R.string.foreground_connection_description)
        ).apply { addAction(getConnectionCancelAction(sapService.name)) }

    open fun getSendingDataToWatchNotification(): NotificationCompat.Builder =
        createNotification(
            channelInfo = getSendingDataToWatchChannelInfo(),
            contentTitle = context.getString(R.string.foreground_sending_title),
            contentText = context.getString(R.string.foreground_sending_description)
        ).apply { addAction(getSendingDataCancelAction(sapService.name)) }

    open fun getCommonChannelInfo() =
        ChannelInfo(FOREGROUND_CHANNEL_ID, context.getString(R.string.foreground_service_description))

    open fun getCommonNotification(): NotificationCompat.Builder =
        getCommonNotification(getCommonChannelInfo())

    open fun getCommonNotification(channelInfo: ChannelInfo = getCommonChannelInfo()): NotificationCompat.Builder =
        createNotification(
            channelInfo = channelInfo,
            contentTitle = context.getString(R.string.foreground_title),
            contentText = context.getString(R.string.foreground_content),
            iconColor = R.color.icon_color,
            iconId = R.drawable.notification_icon
        )

    open fun getOpenLinkClickAction(address: String): PendingIntent =
        PendingIntentUtils.getActivity(
            context, getRequestCode(FOREGROUND_OPEN_LINK_REQUEST_CODE),
            Intent(context, MainActivity::class.java)
                .apply {
                    action = OPEN_LINK_ACTION
                    putExtra(NOTIFICATION_EXTRA, address)
                }
        )

    open fun getOpenLinkNotification(address: String): NotificationCompat.Builder =
        createNotification(
            channelInfo = getOpenLinkChannelInfo(),
            contentTitle = context.getString(R.string.foreground_open_link_title),
            contentText = address,
            iconColor = R.color.icon_color,
            iconId = R.drawable.notification_icon
        ).apply { setContentIntent(getOpenLinkClickAction(address)) }

    open fun notify(notificationId: Int, notification: Notification) {
        notificationManager.notify(notificationId, notification)
    }

    open fun notify(notificationId: Int, notification: NotificationCompat.Builder) {
        notificationManager.notify(notificationId, notification.build())
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}