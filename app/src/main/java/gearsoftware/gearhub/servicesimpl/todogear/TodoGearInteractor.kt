package gearsoftware.gearhub.servicesimpl.todogear

import gearsoftware.gearhub.servicesimpl.todogear.data.ToodledoRepository
import gearsoftware.gearhub.servicesimpl.todogear.data.model.LoginAccessToken
import gearsoftware.gearhub.servicesimpl.todogear.data.model.User
import io.reactivex.rxjava3.core.Single
import toothpick.InjectConstructor

@InjectConstructor
class TodoGearInteractor(
        private val toodledoRepository: ToodledoRepository
) {

    val token: LoginAccessToken?
        get() = toodledoRepository.token

    fun isLoggedIn(): Boolean =
            toodledoRepository.token != null && !toodledoRepository.token?.accessToken.isNullOrBlank()

    fun getUser(): Single<User> =
            toodledoRepository.getUser()

    fun login(loginAccessToken: LoginAccessToken) {
        toodledoRepository.token = loginAccessToken
    }

    fun logout() =
        toodledoRepository.logout()
}