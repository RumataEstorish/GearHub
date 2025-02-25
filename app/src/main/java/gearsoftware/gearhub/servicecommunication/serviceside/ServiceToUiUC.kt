package gearsoftware.gearhub.servicecommunication.serviceside

import gearsoftware.gearhub.servicecommunication.data.ServiceEventBus
import gearsoftware.gearhub.services.data.model.ServiceRequest
import gearsoftware.gearhub.services.data.model.ServiceResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import toothpick.InjectConstructor

@InjectConstructor
class ServiceToUiUC(
    private val repository: ServiceEventBus
) {
    val onReceivedFromUI: Observable<ServiceRequest> =
        repository.onReceiveFromUI

    operator fun invoke(command: ServiceResponse): Completable =
        repository.sendServiceResponse(command)
}