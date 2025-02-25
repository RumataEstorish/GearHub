package gearsoftware.gearhub.servicesimpl.todogear

import android.content.Intent
import com.google.gson.Gson
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.serviceprovider.AbstractSapWebService
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider
import gearsoftware.gearhub.services.SapService
import gearsoftware.gearhub.services.data.model.LoginResultStatus
import gearsoftware.gearhub.services.data.model.ServiceRequest
import gearsoftware.gearhub.servicesimpl.todogear.TodoGearServiceProvider.Companion.TODOGEAR_NAME
import gearsoftware.gearhub.servicesimpl.todogear.data.model.LoginAccessToken
import gearsoftware.gearhub.servicesimpl.todogear.data.model.User
import gearsoftware.gearhub.servicesimpl.todogear.di.TodoGearModule
import gearsoftware.gearhub.view.oauthlogin.OAuthLoginActivity
import gearsoftware.gearhub.view.oauthlogin.OAuthLoginActivity.Companion.OAUTH_REQUEST
import gearsoftware.gearhub.view.oauthlogin.OAuthRequest
import gearsoftware.sap.data.GearCommands
import gearsoftware.sap.data.GearStates
import gearsoftware.sap.data.model.WatchText
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject

/**
 * Toodledo service
 */
@SapService(
    name = TODOGEAR_NAME,
    haveSettings = false,
    haveAuthorization = true,
    storeUrl = "https://galaxy.store/todogear2",
    description = TodoGearServiceDescription::class
)
class TodoGearServiceProvider : AbstractSapWebService("TodoGearProvider") {

    companion object {
        const val TODOGEAR_NAME = "TodoGear"
        const val TODOGEAR_PREFS = "TODOGEAR_PREFS"
        private const val CLIENT_ID = "YOUR_CLIENT_ID"
        private const val CLIENT_SECRET = "YOUR_CLIENT_SECRET"
        private const val SCOPE = "basic%20tasks%20notes%20outlines%20lists%20write"
        private const val AUTH_URL = "https://api.toodledo.com/3/account/authorize.php"
        private const val REDIRECT_URL = "https://sites.google.com/view/gearsoftware/applications/todogear"
        private const val TOKEN_URL = "https://api.toodledo.com/3/account/token.php"
    }

    private val todoGearNotificationProvider: TodoGearNotificationProvider by inject<TodoGearNotificationProvider>()
    private val presenter: TodoGearPresenter by inject()

    init {
        KTP.openScope(Scopes.APP)
            .openSubScope(Scopes.TODOGEAR)
            .installModules(TodoGearModule(this))
            .inject(this)
    }

    override fun onLogoutIntent() {
        presenter.onLogout()
    }

    override fun onLoginIntent() {
        presenter.onLoginIntent()
    }

    fun startLoginActivity() {
        val intent = Intent()
        intent.putExtra(
            OAUTH_REQUEST, Gson().toJson(
                OAuthRequest(
                    CLIENT_ID, AUTH_URL, TOKEN_URL,
                    REDIRECT_URL, CLIENT_SECRET, SCOPE, "grant_type=authorization_code&f=json"
                )
            )
        )
        startLoginActivity(OAuthLoginActivity::class.java, intent)
    }

    override fun setLoginResult(serviceLoginResult: ServiceRequest.ServiceLoginResult) {
        when (serviceLoginResult.status) {
            LoginResultStatus.SUCCESS -> presenter.onLogin(Gson().fromJson(serviceLoginResult.result, LoginAccessToken::class.java))
            LoginResultStatus.FAIL -> presenter.onLoginFailed()
            LoginResultStatus.RETRY -> {
            }
        }
    }


    override fun onCreate() {
        super.onCreate()

        onGearCommand
            .filter { it is GearCommands.Text }
            .cast(GearCommands.Text::class.java)
            .subscribeBy(
                onNext = {
                    when (it.text.channelId) {
                        SERVICE_CHANNEL_ID -> {
                            when (it.text.data) {
                                AUTH_NEEDED -> presenter.onAuthNeededReceive()
                                REAUTH_NEEDED -> presenter.onReauthNeeded()
                            }
                        }
                    }
                },
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        onGearState
            .filter { it is GearStates.Connected }
            .cast(GearStates.Connected::class.java)
            .subscribeBy(
                onNext = { presenter.onConnectedChanged(it.isConnected) },
                onError = Timber::e
            )
            .addTo(compositeDisposable)
    }

    override fun onDestroy() {
        KTP.closeScope(Scopes.TODOGEAR)
        super.onDestroy()
    }


    fun sendToken(token: LoginAccessToken, connect: Boolean = false) {
        sendText(WatchText(SERVICE_CHANNEL_ID, Gson().toJson(token)), connect)
    }

    fun sendAuthNeeded() {
        sendText(WatchText(SERVICE_CHANNEL_ID, AUTH_NEEDED))
    }

    fun onUser(user: User) {
        sendUserName(user.alias)
    }


    override fun getNotificationProvider(): SapNotificationProvider =
        todoGearNotificationProvider
}