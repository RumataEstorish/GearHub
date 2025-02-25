package gearsoftware.gearhub.servicesimpl.squaregear

import gearsoftware.gearhub.servicesimpl.squaregear.model.User
import io.reactivex.rxjava3.core.Single
import toothpick.InjectConstructor

@InjectConstructor
class FourSquareInteractor(
    private val fourSquareRepository: FourSquareRepository
) {

    val accessToken: String
        get() = fourSquareRepository.accessToken

    val isLoggedIn: Boolean
        get() = fourSquareRepository.isLoggedIn

    fun getUser(): Single<User> =
        fourSquareRepository.getUser()

    fun login(accessToken: String) {
        fourSquareRepository.accessToken = accessToken
    }

    fun logout() {
        fourSquareRepository.logout()
    }

}