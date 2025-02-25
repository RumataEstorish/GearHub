package gearsoftware.gearhub.view.servicelist

import gearsoftware.gearhub.servicecommunication.uiside.UiToServiceUC
import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.services.ServicesManager
import gearsoftware.gearhub.services.data.model.ServiceRequest
import gearsoftware.sap.data.ISchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.InjectConstructor

/**
 * Accounts presenter
 */
@InjectConstructor
class ServiceListPresenter(
    private val view: IServiceListView,
    private val serviceManager: ServicesManager,
    private val serviceUiUC: UiToServiceUC,
    private val schedulers: ISchedulers
) : IServiceListPresenter {

    private val compositeDisposable = CompositeDisposable()

    private fun subscribe() {

        serviceUiUC.onReceiveFromService
            .map { serviceManager.enabledServices }
            .observeOn(schedulers.mainThread)
            .subscribeBy(
                onNext = view::update,
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        serviceUiUC.onServiceRemoved
            .map { serviceManager.enabledServices }
            .observeOn(schedulers.mainThread)
            .subscribeBy(
                onNext = view::update,
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        serviceUiUC.onServiceAdded
            .map { serviceManager.enabledServices }
            .observeOn(schedulers.mainThread)
            .subscribeBy(
                onNext = view::update,
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        view.update(serviceManager.enabledServices)
    }

    override fun onServiceRemoveClick(service: ServiceProxy) {
        serviceUiUC(ServiceRequest.ServiceRemoved(service))
            .subscribeBy(onError = Timber::e)
            .addTo(compositeDisposable)
    }

    override fun onLoginClick(service: ServiceProxy) {
        serviceUiUC(ServiceRequest.ServiceLogin(service))
            .subscribeBy(onError = Timber::e)
            .addTo(compositeDisposable)
    }

    override fun onLogoutClick(service: ServiceProxy) {
        serviceUiUC(ServiceRequest.ServiceLogout(service))
            .subscribeBy(onError = Timber::e)
            .addTo(compositeDisposable)
    }

    override fun onSettingsClick(service: ServiceProxy) {
        serviceUiUC(ServiceRequest.ServiceSettingsOpen(service))
            .subscribeBy(onError = Timber::e)
            .addTo(compositeDisposable)
    }

    override fun onPause() {
        compositeDisposable.clear()
    }

    override fun onResume() {
        subscribe()
    }
}