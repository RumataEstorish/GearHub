package gearsoftware.gearhub.socialgear

import gearsoftware.gearhub.socialgear.data.SocialGearRepository
import gearsoftware.gearhub.socialgear.data.model.VKLoginResult
import gearsoftware.gearhub.socialgear.data.model.VKUser
import io.reactivex.rxjava3.core.Single
import toothpick.InjectConstructor

@InjectConstructor
class SocialGearInteractor(
        private val socialGearRepository: SocialGearRepository
) {

    val isLoggedIn: Boolean
        get() = socialGearRepository.accessToken.isNotBlank()

    val accessToken: String
        get() = socialGearRepository.accessToken

    fun login(accessToken: VKLoginResult) {
        socialGearRepository.accessToken = accessToken.accessToken
    }

    fun getUser(): Single<VKUser> =
            socialGearRepository.getUser()

    fun logout() {
        socialGearRepository.logout()
    }

}
