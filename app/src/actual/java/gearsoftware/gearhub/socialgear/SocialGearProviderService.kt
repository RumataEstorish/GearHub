package gearsoftware.gearhub.socialgear

import android.widget.Toast
import com.google.gson.Gson
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import gearsoftware.gearhub.R
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.serviceprovider.AbstractSapWebService
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider
import gearsoftware.gearhub.services.SapService
import gearsoftware.gearhub.services.data.model.LoginResultStatus
import gearsoftware.gearhub.services.data.model.ServiceRequest
import gearsoftware.gearhub.socialgear.SocialGearProviderService.Companion.SOCIALGEAR_NAME
import gearsoftware.gearhub.socialgear.data.model.VKLoginResult
import gearsoftware.gearhub.socialgear.data.model.VKUser
import gearsoftware.gearhub.socialgear.di.SocialGearModule
import gearsoftware.sap.data.GearCommands
import gearsoftware.sap.data.GearStates
import gearsoftware.sap.data.model.WatchText
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject


/**
 * Vkontakte service
 */
@SapService(
    name = SOCIALGEAR_NAME,
    haveAuthorization = true,
    haveSettings = false,
    storeUrl = "https://galaxy.store/socialg2",
    description = SocialGearServiceDescription::class
)
class SocialGearProviderService : AbstractSapWebService("SocialGearProvider") {

    companion object {
        const val SOCIALGEAR_NAME = "SocialGear"
        const val SOCIALGEAR_PREFS = "SOCIAL_GEAR_PREFS"
    }

    private val presenter: SocialGearPresenter by inject()
    private val socialGearNotificationProvider: SocialGearNotificationProvider by inject()

    private val vkAccessTokenTracker: VKTokenExpiredHandler = object : VKTokenExpiredHandler {
        override fun onTokenExpired() =
            presenter.onTokenExpired()
    }

    init {
        KTP.openScopes(Scopes.APP, this)
            .installModules(SocialGearModule(this))
            .inject(this)
    }

    override fun setLoginResult(serviceLoginResult: ServiceRequest.ServiceLoginResult) {
        when (serviceLoginResult.status) {
            LoginResultStatus.SUCCESS -> presenter.onLogin(Gson().fromJson(serviceLoginResult.result, VKLoginResult::class.java))
            LoginResultStatus.FAIL -> presenter.onLoginFailed()
            else -> Unit
        }
    }


    override fun onLogoutIntent() {
        presenter.onLogoutIntent()
    }


    override fun onLoginIntent() {
        presenter.onLoginIntent()
    }

    fun startLoginActivity() {
        startLoginActivity(LoginActivity::class.java)
    }

    override fun onCreate() {
        super.onCreate()

        VK.initialize(applicationContext)

        onGearCommand
            .filter { it is GearCommands.Text }
            .subscribeBy(
                onNext = {
                    when ((it as GearCommands.Text).text.channelId) {
                        SERVICE_CHANNEL_ID -> {
                            when (it.text.data) {
                                AUTH_NEEDED -> presenter.onAuthNeeded()
                            }
                        }
                    }
                }, onError = Timber::e
            )
            .addTo(compositeDisposable)


        onGearState
            .filter { it is GearStates.Connected }
            .cast(GearStates.Connected::class.java)
            .subscribeBy(
                onNext = { presenter.onConnectionChanged(it.isConnected) },
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        VK.addTokenExpiredHandler(vkAccessTokenTracker)
    }

    override fun onDestroy() {
        KTP.closeScope(this)
        VK.removeTokenExpiredHandler(vkAccessTokenTracker)
        presenter.onDestroy()
        super.onDestroy()
    }


    fun setUser(user: VKUser) {
        if (user.firstName != null && user.lastName != null) {
            sendUserName("${user.firstName} ${user.lastName}")
        }
        if (user.lastName == null && user.firstName != null) {
            sendUserName(user.firstName)
        }
    }

    fun sendAccessToken(accessToken: String) {
        sendText(WatchText(SERVICE_CHANNEL_ID, accessToken))
    }

    fun sendAuthNeeded() {
        sendText(WatchText(SERVICE_CHANNEL_ID, AUTH_NEEDED))
    }

    fun showLoginError() {
        Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show()
    }

    override fun getNotificationProvider(): SapNotificationProvider =
        socialGearNotificationProvider
}