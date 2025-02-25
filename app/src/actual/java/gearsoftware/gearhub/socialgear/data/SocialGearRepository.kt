package gearsoftware.gearhub.socialgear.data

import android.content.SharedPreferences
import com.vk.api.sdk.VK
import gearsoftware.gearhub.serviceprovider.AccessTokenRepository
import gearsoftware.gearhub.socialgear.data.model.VKUser
import gearsoftware.gearhub.socialgear.data.model.VKUserRequest
import gearsoftware.sap.data.ISchedulers
import io.reactivex.rxjava3.core.Single
import toothpick.InjectConstructor

@InjectConstructor
class SocialGearRepository(
        sharedPreferences: SharedPreferences,
        private val schedulers: ISchedulers
) : AccessTokenRepository(sharedPreferences) {

    fun getUser(): Single<VKUser> =
            Single.fromCallable { VK.executeSync(VKUserRequest()) }
                    .subscribeOn(schedulers.io)


    override fun logout() {
        super.logout()
        VK.logout()
    }
}
