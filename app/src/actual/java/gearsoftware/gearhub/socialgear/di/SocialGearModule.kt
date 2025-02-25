package gearsoftware.gearhub.socialgear.di

import android.content.SharedPreferences
import gearsoftware.gearhub.di.BaseModule
import gearsoftware.gearhub.serviceprovider.AbstractSapService
import gearsoftware.gearhub.socialgear.SocialGearPresenter
import gearsoftware.gearhub.socialgear.SocialGearProviderService
import gearsoftware.gearhub.socialgear.di.providers.SharedPreferencesProvider
import toothpick.ktp.binding.bind

class SocialGearModule(
        socialGearProviderService: SocialGearProviderService
) : BaseModule() {
    init {
        bind<AbstractSapService>().toInstance(socialGearProviderService)
        bind<SocialGearProviderService>().toInstance(socialGearProviderService)
        bind<SocialGearPresenter>()
        bind<SharedPreferences>().toProvider(SharedPreferencesProvider::class).providesSingleton()
    }
}