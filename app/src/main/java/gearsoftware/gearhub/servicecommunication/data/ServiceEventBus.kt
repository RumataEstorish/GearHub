package gearsoftware.gearhub.servicecommunication.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import gearsoftware.gearhub.serviceprovider.AbstractSapService
import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.services.data.model.ServiceRequest
import gearsoftware.gearhub.services.data.model.ServiceResponse
import gearsoftware.sap.data.ISchedulers
import gearsoftware.sap.utils.safeOnComplete
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import toothpick.InjectConstructor

@InjectConstructor
class ServiceEventBus(
    private val context: Context,
    private val schedulers: ISchedulers
) {
    private val onReceiveFromUISubject: BehaviorSubject<ServiceRequest> = BehaviorSubject.create()
    val onReceiveFromUI: Observable<ServiceRequest> =
        onReceiveFromUISubject.hide()
            .subscribeOn(schedulers.single)

    private val onReceiveFromServiceSubject: BehaviorSubject<ServiceResponse> = BehaviorSubject.create()
    val onReceiveFromService: Observable<ServiceResponse> =
        onReceiveFromServiceSubject.hide()
            .subscribeOn(schedulers.single)

    fun sendServiceRequest(serviceRequest: ServiceRequest): Completable =
        startService(serviceRequest.service)
            .doOnComplete { onReceiveFromUISubject.onNext(serviceRequest) }
            .subscribeOn(schedulers.single)

    fun sendServiceResponse(serviceResponse: ServiceResponse): Completable =
        Completable.fromAction {
            onReceiveFromServiceSubject.onNext(serviceResponse)
        }
            .subscribeOn(schedulers.single)


    private fun getServiceIntent(service: ServiceProxy, action: String = ""): Intent =
        Intent(action).apply {
            setClassName(context.packageName, service.className)
            putExtra(AbstractSapService.SERVICE_NAME_EXTRA, service.name)
        }

    private fun startService(service: ServiceProxy): Completable =
        Completable.create { emitter ->
            context.bindService(getServiceIntent(service), object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, sc: IBinder?) {
                    (sc as? AbstractSapService.SapServiceBinder)?.let {
                        if (it.serviceName == service.name) {
                            emitter.safeOnComplete()
                        }
                    }
                }

                override fun onServiceDisconnected(name: ComponentName) {
                }
            }, Context.BIND_AUTO_CREATE)
        }

}