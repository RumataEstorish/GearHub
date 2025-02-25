package gearsoftware.gearhub.servicesimpl.enotegear.di

import android.content.SharedPreferences
import com.evernote.client.android.EvernoteSession
import gearsoftware.gearhub.di.BaseModule
import gearsoftware.gearhub.servicesimpl.enotegear.*
import gearsoftware.gearhub.servicesimpl.enotegear.di.providers.*
import toothpick.ktp.binding.bind

class ENoteGearModule : BaseModule() {
    init {
        bind<SharedPreferences>().toProvider(SharedPreferencesProvider::class).providesSingleton()
        bind<EvernoteSession>().toProvider(EvernoteSessionProvider::class).providesSingleton()
        bind<Settings>().singleton()
        bind<IEvernoteRepository>().toClass<EvernoteRepository>()
    }
}