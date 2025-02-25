package gearsoftware.gearhub.servicesimpl.squaregear

import android.content.SharedPreferences
import com.google.gson.Gson
import gearsoftware.gearhub.serviceprovider.AccessTokenRepository
import gearsoftware.gearhub.servicesimpl.squaregear.data.FourSquareAPI
import gearsoftware.gearhub.servicesimpl.squaregear.model.User
import gearsoftware.sap.data.ISchedulers
import io.reactivex.rxjava3.core.Single
import toothpick.InjectConstructor

@InjectConstructor
class FourSquareRepository(
        private val fourSquareAPI: FourSquareAPI,
        private val sharedPreferences: SharedPreferences,
        private val schedulerProvider: ISchedulers
) : AccessTokenRepository(sharedPreferences) {

    companion object {
        private const val USER_PREF = "USER_PREF"
    }

    init {
        sharedPreferences.getString(USER_PREF, null)?.let {
            user = Gson().fromJson(it, User::class.java)
        }
    }

    var user: User? = null
        private set(value) {
            field = value
            sharedPreferences.edit().putString(USER_PREF, Gson().toJson(value)).apply()
        }

    fun getUser(): Single<User> =
            fourSquareAPI.getUser(Constants.API_VERSION, accessToken, Constants.MODE)
                    .map { it.response.user }
                    .doOnSuccess { user = it }
                    .subscribeOn(schedulerProvider.io)
}