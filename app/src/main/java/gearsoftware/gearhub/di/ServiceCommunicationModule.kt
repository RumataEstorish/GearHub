package gearsoftware.gearhub.di

import gearsoftware.gearhub.servicecommunication.data.ServiceEventBus
import gearsoftware.gearhub.servicecommunication.uiside.ReceiveNotificationActionUC
import gearsoftware.gearhub.servicecommunication.serviceside.ServiceToUiUC
import gearsoftware.gearhub.servicecommunication.uiside.UiToServiceUC
import toothpick.config.Module
import toothpick.ktp.binding.bind

class ServiceCommunicationModule : Module() {
    init {
        bind<UiToServiceUC>().singleton()
        bind<ReceiveNotificationActionUC>().singleton()

        bind<ServiceToUiUC>().singleton()
        bind<ServiceEventBus>().singleton()
    }
}