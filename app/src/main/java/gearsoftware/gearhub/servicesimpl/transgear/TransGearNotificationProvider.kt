package gearsoftware.gearhub.servicesimpl.transgear

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import gearsoftware.gearhub.R
import gearsoftware.gearhub.serviceprovider.*
import gearsoftware.gearhub.serviceprovider.AbstractSapService.Companion.SERVICE_NAME_EXTRA
import gearsoftware.gearhub.servicesimpl.transgear.CancelTransferDialog.Companion.CANCEL_ALL
import gearsoftware.gearhub.servicesimpl.transgear.CancelTransferDialog.Companion.CANCEL_ONE
import gearsoftware.sap.utils.PendingIntentUtils
import toothpick.InjectConstructor
import java.io.File

@InjectConstructor
class TransGearNotificationProvider(
    private val context: Context,
    private val sapService: AbstractSapService
) : SapNotificationProvider(context, sapService) {

    companion object {
        private const val TRANSGEAR_NOTIFICATIONS_GROUP = "TRANSGEAR_NOTIFICATIONS_GROUP"
        private const val FOREGROUND_CONNECTION_CHANNEL_ID = "TRANSGEAR_CONNECTION_CHANNEL_ID"
        private const val FOREGROUND_TRANSFER_CHANNEL_ID = "TRANSGEAR_TRANSFER_CHANNEL_ID"
        private const val FOREGROUND_CHANNEL_ID = "TRANSGEAR_FOREGROUND_CHANNEL_ID"
        private const val CANCEL_REQUEST_CODE = 91834
        private const val CANCEL_ALL_REQUEST_CODE = 91835
        private const val CHANNEL_GROUP_ID = "TransGear"
    }

    private fun getCancelAction(): NotificationCompat.Action =
        NotificationCompat.Action(
            R.drawable.empty,
            context.getString(android.R.string.cancel),
            PendingIntentUtils.getActivity(
                context,
                CANCEL_REQUEST_CODE,
                Intent(context, CancelTransferDialog::class.java).apply {
                    putExtra(CANCEL_ONE, true)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
        )

    private fun getCancelAllAction(): NotificationCompat.Action =
        NotificationCompat.Action(
            R.drawable.empty,
            context.getString(R.string.cancel_all),
            PendingIntentUtils.getActivity(
                context,
                CANCEL_ALL_REQUEST_CODE,
                Intent(context, CancelTransferDialog::class.java).apply {
                    putExtra(CANCEL_ALL, true)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        )

    private fun getSummary(builder: NotificationCompat.Builder, currentFile: String?, files: List<String>): NotificationCompat.Builder {
        val fileName: String = if (currentFile.isNullOrBlank()) {
            ""
        } else {
            File(currentFile).name
        }

        val notificationCompat = NotificationCompat.InboxStyle(builder)
        if (files.size > 5) {
            for (i in 0..4) {
                notificationCompat.addLine(files[i])
            }
            notificationCompat.setSummaryText("+${files.size - 5} ${context.resources.getString(R.string.more_files)}")
        } else {
            for (element in files) {
                notificationCompat.addLine(element)
            }
            notificationCompat.setSummaryText(null)
        }
        return builder
    }

    @SuppressLint("InlinedApi")
    private fun getTransferChannelInfo(): ChannelInfo =
        getCommonChannelInfo().copy(id = FOREGROUND_TRANSFER_CHANNEL_ID, name = context.getString(R.string.transgear_channel_name), importance = NotificationManager.IMPORTANCE_DEFAULT)

    override fun getCommonChannelInfo(): ChannelInfo =
        super.getCommonChannelInfo().copy(id = FOREGROUND_CHANNEL_ID, group = ChannelGroup(CHANNEL_GROUP_ID, context.getString(R.string.transgear)))

    override fun getCommonNotification(channelInfo: ChannelInfo): NotificationCompat.Builder =
        super.getCommonNotification(channelInfo)
            .setColor(ContextCompat.getColor(context, R.color.transgear_foreground_icon_color))
            .setSmallIcon(R.drawable.transgear_notification_ic)

    override fun getConnectionChannelInfo(): ChannelInfo =
        getCommonChannelInfo().copy(id = FOREGROUND_CONNECTION_CHANNEL_ID, name = context.getString(R.string.transgear_foreground_connection_channel_name))

    override fun getConnectionToWatchNotification(): NotificationCompat.Builder =
        super.getConnectionToWatchNotification()
            .setSmallIcon(R.drawable.transgear_notification_ic)
            .setColor(ContextCompat.getColor(context, R.color.transgear_foreground_icon_color))

    override fun getSendingDataToWatchNotification(): NotificationCompat.Builder =
        super.getSendingDataToWatchNotification()
            .setSmallIcon(R.drawable.transgear_notification_ic)
            .setColor(ContextCompat.getColor(context, R.color.transgear_foreground_icon_color))


    override fun getConnectionCancelAction(serviceName: String): NotificationCompat.Action =
        NotificationCompat.Action(
            R.drawable.empty,
            context.getString(android.R.string.cancel),
            PendingIntentUtils.getService(
                context,
                FOREGROUND_CANCEL_CONNECT_REQUEST_CODE,
                Intent(context, NotificationsSignalService::class.java).apply {
                    action = TransGearProviderService.FOREGROUND_CANCEL_CONNECT_ACTION
                    putExtra(SERVICE_NAME_EXTRA, sapService.name)
                }
            )
        )

    fun getTransferCompleteNotification(): NotificationCompat.Builder =
        getCommonNotification(getTransferChannelInfo())
            .setGroup(TRANSGEAR_NOTIFICATIONS_GROUP)
            .setContentTitle(context.getString(R.string.transgear_completed))
            .setProgress(100, 100, false)

    fun getTransferNotificationPrepare(currentFile: String?, files: List<String>): NotificationCompat.Builder {
        val notification = getCommonNotification(getTransferChannelInfo())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle(context.getString(R.string.transgear_transfer_in_progress))
            .addAction(getCancelAction())

        if (files.size > 1) {
            notification.addAction(getCancelAllAction())
        }
        getSummary(notification, currentFile, files)

        return notification
    }

    fun getTransferNotificationProgress(progress: Int, currentFile: String?, files: List<String>): NotificationCompat.Builder =
        getTransferNotificationPrepare(currentFile, files)
            .setProgress(100, progress, false)

    fun getTransferNotificationError(message: String): NotificationCompat.Builder =
        getCommonNotification(getTransferChannelInfo())
            .setContentTitle(message)

}