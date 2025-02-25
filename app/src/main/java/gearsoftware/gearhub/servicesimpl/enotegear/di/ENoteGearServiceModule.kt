package gearsoftware.gearhub.servicesimpl.enotegear.di

import gearsoftware.gearhub.servicesimpl.enotegear.ENoteGearPresenter
import gearsoftware.gearhub.servicesimpl.enotegear.ENoteGearServiceProvider
import toothpick.config.Module
import toothpick.ktp.binding.bind

class ENoteGearServiceModule(
        eNoteGearServiceProvider: ENoteGearServiceProvider
) : Module() {
    init {
        bind<ENoteGearServiceProvider>().toInstance(eNoteGearServiceProvider)
        bind<ENoteGearPresenter>()
    }
}