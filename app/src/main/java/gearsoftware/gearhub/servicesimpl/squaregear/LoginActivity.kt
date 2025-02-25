package gearsoftware.gearhub.servicesimpl.squaregear

import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.Toast
import com.foursquare.android.nativeoauth.FoursquareOAuth
import com.foursquare.android.nativeoauth.model.AuthCodeResponse
import gearsoftware.gearhub.R
import gearsoftware.gearhub.services.data.model.LoginResultStatus
import gearsoftware.gearhub.view.oauthlogin.ServiceLoginActivity


/**
 * Foursquare onLoginIntent activity
 */

class LoginActivity : ServiceLoginActivity() {

    companion object {
        private const val REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 201
        private const val REQUEST_CODE_FSQ_CONNECT = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = FoursquareOAuth.getConnectIntent(applicationContext, Constants.CLIENT_ID, true)
        if (FoursquareOAuth.isPlayStoreIntent(intent)) {
            Toast.makeText(applicationContext, getString(R.string.app_not_installed_message), Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
            return
        }
        clearCookies()
        startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT)
    }

    @Suppress("DEPRECATION")
    private fun clearCookies() {
        val cookieSyncMngr = CookieSyncManager.createInstance(this)
        cookieSyncMngr.startSync()
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()
        cookieManager.removeSessionCookie()
        cookieSyncMngr.stopSync()
        cookieSyncMngr.sync()
    }

    @Deprecated("Migrate to new activity result")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_FSQ_CONNECT -> {
                val codeResponse: AuthCodeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data)
                val code = codeResponse.code
                if (codeResponse.exception != null) {
                    finish()
                    return
                }
                val intent = FoursquareOAuth.getTokenExchangeIntent(this, Constants.CLIENT_ID, Constants.CLIENT_SECRET, code)
                startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE)
            }
            REQUEST_CODE_FSQ_TOKEN_EXCHANGE -> {
                val response = FoursquareOAuth.getTokenFromResult(resultCode, data)
                if (response.exception != null) {
                    sendLoginResult(serviceName, requestCode, resultCode, null, response.exception.toString(), LoginResultStatus.FAIL)
                    Toast.makeText(this, response.exception.message, Toast.LENGTH_SHORT).show()
                    return
                }
                sendLoginResult(serviceName, requestCode, resultCode, null, response.accessToken, LoginResultStatus.SUCCESS)
            }
        }
    }
}