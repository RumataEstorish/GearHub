package gearsoftware.gearhub.servicesimpl.transgear.di

import android.content.SharedPreferences
import gearsoftware.gearhub.di.BaseModule
import gearsoftware.gearhub.serviceprovider.AbstractSapService
import gearsoftware.gearhub.servicesimpl.transgear.TransGearPresenter
import gearsoftware.gearhub.servicesimpl.transgear.TransGearProviderService
import gearsoftware.gearhub.servicesimpl.transgear.data.repository.*
import gearsoftware.gearhub.servicesimpl.transgear.data.repository.QueueRepository
import gearsoftware.gearhub.servicesimpl.transgear.data.repository.SaftRepository
import gearsoftware.gearhub.servicesimpl.transgear.data.sources.IQueueSource
import gearsoftware.gearhub.servicesimpl.transgear.data.sources.ISAFTSource
import gearsoftware.gearhub.servicesimpl.transgear.data.sources.QueueSource
import gearsoftware.gearhub.servicesimpl.transgear.di.providers.*
import gearsoftware.sap.ISap
import toothpick.ktp.binding.bind

class TransGearModule(
        sap: ISap,
        saftSource: ISAFTSource,
        transGearProviderService: TransGearProviderService
) : BaseModule() {
    init {
        bind<AbstractSapService>().toInstance(transGearProviderService)
        bind<TransGearProviderService>().toInstance(transGearProviderService)
        bind<TransGearPresenter>()
        bind<ISAFTSource>().toInstance(saftSource)
        bind<ISap>().toInstance(sap)
        bind<SharedPreferences>().toProvider(SharedPreferencesProvider::class).providesSingleton()
        bind<IQueueSource>().toClass<QueueSource>()
        bind<IQueueRepository>().toClass<QueueRepository>()
        bind<GearMapper>().toInstance(GearMapper)
        bind<ISaftRepository>().toClass<SaftRepository>()
    }
}