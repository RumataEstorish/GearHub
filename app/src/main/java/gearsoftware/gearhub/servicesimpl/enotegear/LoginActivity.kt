package gearsoftware.gearhub.servicesimpl.enotegear

import android.os.Bundle
import android.widget.Toast
import com.evernote.client.android.EvernoteSession
import com.evernote.client.android.login.EvernoteLoginFragment
import gearsoftware.gearhub.R
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.services.data.model.LoginResultStatus
import gearsoftware.gearhub.servicesimpl.enotegear.di.ENoteGearModule
import gearsoftware.gearhub.view.oauthlogin.ServiceLoginActivity
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject

/**
 * Evernote onLoginIntent activity
 */
class LoginActivity : ServiceLoginActivity(), EvernoteLoginFragment.ResultCallback {

    private val evernoteSession: EvernoteSession by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KTP.openScope(Scopes.APP)
            .openSubScope(Scopes.ENOTEGEAR)
            .installModules(ENoteGearModule())
            .inject(this)

        evernoteSession.authenticate(this)

        Toast.makeText(this, R.string.enotegear_login_warn, Toast.LENGTH_LONG).show()
    }

    override fun onLoginFinished(successful: Boolean) {
        if (successful) {
            sendLoginResult(
                serviceName!!,
                EvernoteSession.REQUEST_CODE_LOGIN,
                RESULT_OK,
                null,
                LoginResultStatus.SUCCESS
            )
        } else {
            sendLoginResult(
                serviceName!!,
                EvernoteSession.REQUEST_CODE_LOGIN,
                RESULT_CANCELED,
                null,
                LoginResultStatus.FAIL
            )
        }
        finish()
    }
}