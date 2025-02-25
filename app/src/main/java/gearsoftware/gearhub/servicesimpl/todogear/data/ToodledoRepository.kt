package gearsoftware.gearhub.servicesimpl.todogear.data

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.google.gson.Gson
import gearsoftware.gearhub.servicesimpl.todogear.data.model.LoginAccessToken
import gearsoftware.gearhub.servicesimpl.todogear.data.model.User
import gearsoftware.sap.data.ISchedulers
import io.reactivex.rxjava3.core.Single
import toothpick.InjectConstructor

@InjectConstructor
class ToodledoRepository(
    private val toodleDoAPI: ToodleDoAPI,
    private val sharedPreferences: SharedPreferences,
    private val schedulers: ISchedulers
) {

    companion object {
        private const val TOKEN = "TOKEN"
    }

    var token: LoginAccessToken?
        get() =
            sharedPreferences.getString(TOKEN, null)?.let {
                Gson().fromJson(it, LoginAccessToken::class.java)
            }
        @SuppressLint("ApplySharedPref")
        set(value) {
            sharedPreferences.edit().putString(TOKEN, Gson().toJson(value)).commit()
        }


    fun getUser(): Single<User> =
        token?.let {
            if (it.accessToken.isNotBlank()) {
                toodleDoAPI.getUser(token!!.accessToken)
                    .subscribeOn(schedulers.io)
            } else {
                null
            }
        } ?: Single.just(User())


    fun logout() =
        sharedPreferences.edit().remove(TOKEN).commit()
}