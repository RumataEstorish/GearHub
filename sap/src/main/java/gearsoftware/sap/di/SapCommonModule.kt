package gearsoftware.sap.di

import gearsoftware.sap.data.ISchedulers
import gearsoftware.sap.data.SchedulerProvider
import toothpick.config.Module
import toothpick.ktp.binding.bind

class SapCommonModule: Module() {
    init {
        bind<ISchedulers>().toClass<SchedulerProvider>()
    }
}