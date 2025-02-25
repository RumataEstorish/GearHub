package gearsoftware.gearhub.servicesimpl.taskgear

import android.content.Intent
import com.google.gson.Gson
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.serviceprovider.AbstractSapWebService
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider
import gearsoftware.gearhub.services.SapService
import gearsoftware.gearhub.services.data.model.LoginResultStatus
import gearsoftware.gearhub.services.data.model.ServiceRequest
import gearsoftware.gearhub.servicesimpl.taskgear.TaskGearServiceProvider.Companion.TASKGEAR_NAME
import gearsoftware.gearhub.servicesimpl.taskgear.data.model.LoginAccessToken
import gearsoftware.gearhub.servicesimpl.taskgear.data.model.User
import gearsoftware.gearhub.servicesimpl.taskgear.di.TaskGearModule
import gearsoftware.gearhub.view.oauthlogin.OAuthLoginActivity
import gearsoftware.gearhub.view.oauthlogin.OAuthLoginActivity.Companion.OAUTH_REQUEST
import gearsoftware.gearhub.view.oauthlogin.OAuthRequest
import gearsoftware.sap.data.GearCommands
import gearsoftware.sap.data.GearStates
import gearsoftware.sap.data.model.WatchText
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject
import java.util.concurrent.TimeUnit

/**
 * Service for TaskGear
 */
@SapService(
    name = TASKGEAR_NAME,
    haveAuthorization = true,
    haveSettings = false,
    storeUrl = "https://galaxy.store/taskgear2",
    description = TaskGearServiceDescription::class
)
class TaskGearServiceProvider : AbstractSapWebService("TaskGearProvider") {

    companion object {
        const val TASKGEAR_NAME = "TaskGear"
        const val TASKGEAR_PREFS = "TASKGEAR_PREFS"

        private const val CLIENT_ID = "YOUR_CLIENT_ID"
        private const val CLIENT_SECRET = "YOUR_CLIENT_SECRET"
        private const val SCOPE = "task:add,data:read,data:read_write,data:delete,project:delete"
        private const val AUTH_URL = "https://todoist.com/oauth/authorize/"
        private const val REDIRECT_URL = "https://sites.google.com/view/gearsoftware/applications/taskgear"
        private const val TOKEN_URL = "https://todoist.com/oauth/access_token/"
    }

    private val presenter: TaskGearPresenter by inject()

    private val taskGearNotificationProvider: TaskGearNotificationProvider by inject<TaskGearNotificationProvider>()

    init{
        KTP.openScope(Scopes.APP)
            .openSubScope(Scopes.TASKGEAR)
            .installModules(TaskGearModule(this))
            .inject(this)
    }

    override fun onLogoutIntent() {
        presenter.onLogoutIntent()
    }

    override fun onLoginIntent() {
        presenter.onLoginIntent()
    }

    override fun setLoginResult(serviceLoginResult: ServiceRequest.ServiceLoginResult) {
        when (serviceLoginResult.status) {
            LoginResultStatus.SUCCESS -> presenter.onLogin(Gson().fromJson(serviceLoginResult.result, LoginAccessToken::class.java).accessToken)
            LoginResultStatus.FAIL -> presenter.onLoginFailed()
            LoginResultStatus.RETRY ->
                Observable.timer(1, TimeUnit.SECONDS)
                    .subscribeBy(
                        onNext = { onLoginIntent() },
                        onError = Timber::e
                    )
                    .addTo(compositeDisposable)
        }
    }

    fun startLoginActivity() {
        val intent = Intent()
        intent.putExtra(
            OAUTH_REQUEST, Gson().toJson(
                OAuthRequest(
                    CLIENT_ID, AUTH_URL, TOKEN_URL,
                    REDIRECT_URL, CLIENT_SECRET, SCOPE, String.format("redirect_uri=%s", REDIRECT_URL)
                )
            )
        )
        startLoginActivity(OAuthLoginActivity::class.java, intent)
    }

    override fun onCreate() {
        super.onCreate()

        onGearCommand
            .filter { it is GearCommands.Text }
            .cast(GearCommands.Text::class.java)
            .subscribeBy(
                onNext = {
                    when (it.text.data) {
                        AUTH_NEEDED -> presenter.onAuthNeededReceived()
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
        KTP.closeScope(Scopes.TASKGEAR)
        super.onDestroy()
    }

    fun onUser(user: User) {
        sendUserName(user.fullName)
    }

    fun sendAccessTokenToWatch(accessToken: String, connect: Boolean = false) {
        sendText(WatchText(SERVICE_CHANNEL_ID, accessToken), connect)
    }

    fun sendAuthNeededToWatch() {
        sendText(WatchText(SERVICE_CHANNEL_ID, AUTH_NEEDED))
    }

    override fun getNotificationProvider(): SapNotificationProvider =
        taskGearNotificationProvider

}
