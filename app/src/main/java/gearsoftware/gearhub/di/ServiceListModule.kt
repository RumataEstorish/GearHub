package gearsoftware.gearhub.di

import gearsoftware.gearhub.view.servicelist.IServiceListView
import gearsoftware.gearhub.view.servicelist.IServiceListPresenter
import gearsoftware.gearhub.view.servicelist.ServiceListPresenter
import toothpick.config.Module
import toothpick.ktp.binding.bind

class ServiceListModule(
        serviceListView: IServiceListView
): Module() {
    init {
        bind<IServiceListView>().toInstance(serviceListView)
        bind<IServiceListPresenter>().toClass<ServiceListPresenter>()
    }
}