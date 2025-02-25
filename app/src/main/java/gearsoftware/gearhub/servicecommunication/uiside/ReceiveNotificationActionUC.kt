package gearsoftware.gearhub.servicecommunication.uiside

import gearsoftware.gearhub.servicecommunication.data.ServiceEventBus
import gearsoftware.gearhub.services.data.model.ServiceRequest
import io.reactivex.rxjava3.core.Completable
import toothpick.InjectConstructor

@InjectConstructor
class ReceiveNotificationActionUC(
    private val repository: ServiceEventBus
) {
    operator fun invoke(serviceRequest: ServiceRequest.ServiceNotificationAction): Completable =
        repository.sendServiceRequest(serviceRequest)
}