package gearsoftware.gearhub.di

import android.content.Context
import android.content.SharedPreferences
import gearsoftware.gearhub.di.providers.*
import gearsoftware.gearhub.services.ServicesManager
import gearsoftware.gearhub.services.data.ServiceList
import gearsoftware.gearhub.services.data.ServicesRepository
import gearsoftware.gearhub.services.data.sources.ServicesInternalSource
import toothpick.ktp.binding.bind

class AppModule(
        context: Context
) : BaseModule() {

    init {
        bind<Context>().toInstance(context)
        bind<SharedPreferences>().toInstance(context.getSharedPreferences("GEARHUB", Context.MODE_PRIVATE))
        bind<ServicesManager>().singleton()
        bind<ServicesRepository>().singleton()
        bind<ServicesInternalSource>().toProvider(ServicesInternalSourceProvider::class)
        bind<ServiceList>().withName(InternalServices::class).toProvider(InternalServicesProvider::class).providesSingleton()
    }
}