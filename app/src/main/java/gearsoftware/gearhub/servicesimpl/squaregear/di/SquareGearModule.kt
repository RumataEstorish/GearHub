package gearsoftware.gearhub.servicesimpl.squaregear.di

import android.content.SharedPreferences
import gearsoftware.gearhub.di.BaseModule
import gearsoftware.gearhub.serviceprovider.AbstractSapService
import gearsoftware.gearhub.servicesimpl.squaregear.*
import gearsoftware.gearhub.servicesimpl.squaregear.data.FourSquareAPI
import gearsoftware.gearhub.servicesimpl.squaregear.di.providers.*
import toothpick.ktp.binding.bind

class SquareGearModule(
        fourSquareAccessoryProvider: FourSquareAccessoryProvider
) : BaseModule() {
    init {
        bind<AbstractSapService>().toInstance(fourSquareAccessoryProvider)
        bind<FourSquareAccessoryProvider>().toInstance(fourSquareAccessoryProvider)
        bind<FourSquarePresenter>()
        bind<SharedPreferences>().toProvider(SharedPreferencesProvider::class).providesSingleton()
        bind<FourSquareAPI>().toProvider(FourSquareAPIProvider::class)
    }
}