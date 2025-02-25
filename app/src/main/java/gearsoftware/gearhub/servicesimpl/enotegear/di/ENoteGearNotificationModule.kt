package gearsoftware.gearhub.servicesimpl.enotegear.di

import gearsoftware.gearhub.serviceprovider.AbstractSapService
import toothpick.config.Module
import toothpick.ktp.binding.bind

class ENoteGearNotificationModule(
        sapService: AbstractSapService
) : Module() {
    init {
        bind<AbstractSapService>().toInstance(sapService)
    }
}