package gearsoftware.gearhub.socialgear

import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKAuthException
import gearsoftware.gearhub.services.data.model.LoginResultStatus
import gearsoftware.gearhub.socialgear.data.model.VKLoginResult
import gearsoftware.gearhub.view.oauthlogin.ServiceLoginActivity

/**
 * SocialGear onLoginIntent activity
 */
class LoginActivity : ServiceLoginActivity() {

    companion object {
        private val PERMISSIONS = listOf(VKScope.WALL, VKScope.OFFLINE, VKScope.MESSAGES, VKScope.STATUS, VKScope.PHOTOS, VKScope.FRIENDS, VKScope.AUDIO)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VK.login(this, PERMISSIONS)
    }

    @Deprecated("Migrate to new login result")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        VK.onActivityResult(requestCode, resultCode, data, object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                sendLoginResult(serviceName, requestCode, resultCode, null, Gson().toJson(VKLoginResult(token)), LoginResultStatus.SUCCESS)
            }

            override fun onLoginFailed(authException: VKAuthException) {
                sendLoginResult(serviceName, requestCode, resultCode, null, authException.webViewError.toString(), LoginResultStatus.FAIL)
            }
        })
    }
}