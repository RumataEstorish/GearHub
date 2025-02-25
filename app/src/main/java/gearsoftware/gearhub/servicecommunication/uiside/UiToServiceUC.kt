package gearsoftware.gearhub.servicecommunication.uiside

import gearsoftware.gearhub.servicecommunication.data.ServiceEventBus
import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.serviceprovider.ServiceWebProxy
import gearsoftware.gearhub.services.data.ServicesRepository
import gearsoftware.gearhub.services.data.model.ServiceRequest
import gearsoftware.gearhub.services.data.model.ServiceResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import timber.log.Timber
import toothpick.InjectConstructor

@InjectConstructor
class UiToServiceUC(
    private val serviceCommunicationRepository: ServiceEventBus,
    private val servicesRepository: ServicesRepository
) {

    private val onServiceAddedSubject: BehaviorSubject<ServiceProxy> = BehaviorSubject.create()
    val onServiceAdded: Observable<ServiceProxy> =
        onServiceAddedSubject.hide()

    private val onServiceRemovedSubject: BehaviorSubject<ServiceProxy> = BehaviorSubject.create()
    val onServiceRemoved: Observable<ServiceProxy> =
        onServiceRemovedSubject.hide()

    val onReceiveFromService: Observable<ServiceResponse> =
        serviceCommunicationRepository.onReceiveFromService
            .doOnNext {
                when (it) {
                    is ServiceResponse.ServiceLoggedIn -> servicesRepository.setServiceIsLoggedIn(it.serviceName, true)
                    is ServiceResponse.ServiceLoggedOut -> servicesRepository.setServiceIsLoggedIn(it.serviceName, false)
                    is ServiceResponse.ServiceUserName -> servicesRepository.setServiceUserName(it.serviceName, it.userName)
                }
            }

    operator fun invoke(request: ServiceRequest): Completable =
        when (request) {
            is ServiceRequest.ServiceAdded ->
                servicesRepository.checkService(request.service, true)
                    .doOnComplete { onServiceAddedSubject.onNext(request.service) }
                    .andThen(Completable.defer { serviceCommunicationRepository.sendServiceRequest(request) })
            is ServiceRequest.ServiceRemoved -> {
                servicesRepository.checkService(request.service, false)
                    .doOnComplete { onServiceRemovedSubject.onNext(request.service) }
                    .andThen(
                        Completable.defer {
                            if (request.service is ServiceWebProxy) {
                                serviceCommunicationRepository.sendServiceRequest(request)
                            } else {
                                Completable.complete()
                            }
                        }
                    )
            }
            is ServiceRequest.ServiceLogin -> serviceCommunicationRepository.sendServiceRequest(request)
            is ServiceRequest.ServiceLogout -> serviceCommunicationRepository.sendServiceRequest(request)
            is ServiceRequest.ServiceSettingsOpen -> {
                require(request.service.haveSettings) { "Service have no settings" }
                serviceCommunicationRepository.sendServiceRequest(request)
            }
            is ServiceRequest.ServiceLoginResult -> serviceCommunicationRepository.sendServiceRequest(request)
            is ServiceRequest.ServiceNotificationAction -> serviceCommunicationRepository.sendServiceRequest(request)
        }
}