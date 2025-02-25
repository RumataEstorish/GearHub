package gearsoftware.sap.di

import android.content.Context
import gearsoftware.sap.Sap
import gearsoftware.sap.SapInteractor
import gearsoftware.sap.SapPresenter
import gearsoftware.sap.data.gearhttp.NetRequestRepository
import gearsoftware.sap.data.gearhttp.NetRequestSource
import gearsoftware.sap.data.gearhttp.RequestMapper
import gearsoftware.sap.data.location.LocationMapper
import gearsoftware.sap.data.location.LocationRepository
import gearsoftware.sap.data.location.LocationSource
import gearsoftware.sap.di.provider.*
import toothpick.config.Module
import toothpick.ktp.binding.bind

internal class SapModule(context: Context) : Module() {
    init {
        bind<Context>().toInstance(context)
        bind<SapInteractor>().toProvider(SapInteractorProvider::class).providesSingleton()
        bind<NetRequestRepository>().singleton()
        bind<NetRequestSource>().toProvider(NetRequestSourceProvider::class).providesSingleton()
        bind<RequestMapper>().toInstance(RequestMapper)
        bind<LocationMapper>().toInstance(LocationMapper)
    }
}