package gearsoftware.gearhub.servicesimpl.taskgear.data

import android.content.SharedPreferences
import gearsoftware.gearhub.serviceprovider.AccessTokenRepository
import gearsoftware.gearhub.serviceprovider.IAccessTokenRepository
import gearsoftware.gearhub.servicesimpl.taskgear.data.model.User
import gearsoftware.sap.data.ISchedulers
import io.reactivex.rxjava3.core.Observable
import toothpick.InjectConstructor

@InjectConstructor
class TodoistRepository(
    private val todoistAPI: TodoistAPI,
    sharedPreferences: SharedPreferences,
    private val schedulers: ISchedulers
) : AccessTokenRepository(sharedPreferences), IAccessTokenRepository {

    companion object {
        private const val DEFAULT_SYNC_TOKEN = "*"
    }


    fun getUser(): Observable<User> =
        todoistAPI.getUser(accessToken, DEFAULT_SYNC_TOKEN, "[\"user\"]")
            .map { it.user ?: User() }
            .subscribeOn(schedulers.io)
}