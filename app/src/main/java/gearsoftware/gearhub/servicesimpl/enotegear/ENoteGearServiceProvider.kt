package gearsoftware.gearhub.servicesimpl.enotegear

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.IntentFilter
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.serviceprovider.AbstractSapWebService
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider
import gearsoftware.gearhub.services.SapService
import gearsoftware.gearhub.services.data.model.ServiceRequest
import gearsoftware.gearhub.servicesimpl.enotegear.ENoteGearServiceProvider.Companion.ENOTEGEAR_NAME
import gearsoftware.gearhub.servicesimpl.enotegear.SettingsActivity.Companion.ENOTEGEAR_SETTINGS_ACTIVITY
import gearsoftware.gearhub.servicesimpl.enotegear.di.ENoteGearModule
import gearsoftware.gearhub.servicesimpl.enotegear.di.ENoteGearNotificationModule
import gearsoftware.gearhub.servicesimpl.enotegear.di.ENoteGearServiceModule
import gearsoftware.gearhub.servicesimpl.enotegear.model.SyncStatus
import gearsoftware.sap.data.GearCommands
import gearsoftware.sap.data.model.WatchText
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject

@SapService(
    name = ENOTEGEAR_NAME,
    haveAuthorization = true,
    haveSettings = false,
    storeUrl = "https://galaxy.store/ENoteG",
    description = ENoteGearServiceDescription::class
)
class ENoteGearServiceProvider : AbstractSapWebService("ENOTEGEARSERVICEPROVIDER") {

    companion object {
        private const val FOREGROUND_ID = 12455
        const val FULL_SYNC_CHANNEL_ID = 106
        const val ENOTEGEAR_NAME = "ENoteGear"
        const val ENOTEGEAR_PREF = "ENoteGear"
    }

    private val presenter: ENoteGearPresenter by inject()

    private val eNoteGearNotificationProvider: ENoteGearNotificationProvider by inject()

    private val settingsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val settingsExtra = intent.getStringExtra(SETTINGS_SERVICE_NAME)
            if (settingsExtra != null && settingsExtra == name) {
                val intent1 = Intent(this@ENoteGearServiceProvider, SettingsActivity::class.java)
                    .apply { flags = FLAG_ACTIVITY_NEW_TASK }
                context.startActivity(intent1)
            }
            when (intent.action) {
                ENOTEGEAR_SETTINGS_ACTIVITY -> Unit
            }
        }
    }

    init {
        KTP.openScope(Scopes.APP)
            .openSubScope(Scopes.ENOTEGEAR)
            .installModules(
                ENoteGearModule(),
                ENoteGearServiceModule(this),
                ENoteGearNotificationModule(this)
            )
            .inject(this)
    }

    @SuppressLint("WrongConstant")
    override fun onCreate() {
        onGearCommand
            .filter { it is GearCommands.Text }
            .cast(GearCommands.Text::class.java)
            .subscribeBy(
                onNext = { presenter.onReceive(it.text) },
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        ContextCompat.registerReceiver(this, settingsReceiver, IntentFilter(SERVICE_SETTINGS_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED)
        ContextCompat.registerReceiver(this, settingsReceiver, IntentFilter(SETTINGS_RESULT_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED)
        ContextCompat.registerReceiver(this, settingsReceiver, IntentFilter(ENOTEGEAR_SETTINGS_ACTIVITY), ContextCompat.RECEIVER_NOT_EXPORTED)

        super.onCreate()
    }


    fun sendAuthNeeded() {
        sendText(WatchText(SERVICE_CHANNEL_ID, AUTH_NEEDED))
    }

    fun sendData(data: WatchText) {
        sendText(data)
    }

    override fun onDestroy() {
        unregisterReceiver(settingsReceiver)
        KTP.closeScope(Scopes.ENOTEGEAR)
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onLoginIntent() {
        presenter.onLoginIntent()
    }

    fun startLoginActivity() {
        startLoginActivity(LoginActivity::class.java)
    }

    override fun onLogoutIntent() {
        presenter.onLogout()
    }


    override fun setLoginResult(serviceLoginResult: ServiceRequest.ServiceLoginResult) {
        presenter.onLoginResult()
    }

    fun startLongTermOperation() {
        updateStatus(SyncStatus.IDLE)
    }

    fun stopLongTermOperation() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    fun updateStatus(status: SyncStatus) {
        startForegroundEx(FOREGROUND_ID, eNoteGearNotificationProvider.getSyncNotification(status).build())
    }

    override fun getNotificationProvider(): SapNotificationProvider =
        eNoteGearNotificationProvider

}
