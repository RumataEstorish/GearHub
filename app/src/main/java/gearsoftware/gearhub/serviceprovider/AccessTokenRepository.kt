package gearsoftware.gearhub.serviceprovider

import android.annotation.SuppressLint
import android.content.SharedPreferences

open class AccessTokenRepository(
        private val sharedPreferences: SharedPreferences
) : IAccessTokenRepository {

    companion object {
        const val ACCESS_TOKEN_PREF = "ACCESS_TOKEN"
    }

    override var accessToken: String
        get() = sharedPreferences.getString(ACCESS_TOKEN_PREF, "")!!
        @SuppressLint("ApplySharedPref")
        set(value) {
            sharedPreferences.edit().putString(ACCESS_TOKEN_PREF, value).commit()
        }

    override val isLoggedIn: Boolean
        get() = accessToken.isNotBlank()

    open fun logout() {
        sharedPreferences.edit().remove(ACCESS_TOKEN_PREF).apply()
    }
}