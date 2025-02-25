package gearsoftware.gearhub.view.main

import gearsoftware.gearhub.servicecommunication.uiside.UiToServiceUC
import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.services.SapPermission
import gearsoftware.gearhub.services.ServicesManager
import gearsoftware.gearhub.services.data.model.ServiceRequest
import gearsoftware.gearhub.view.main.model.PermissionType
import gearsoftware.sap.data.ISchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.toObservable
import timber.log.Timber
import toothpick.InjectConstructor

/**
 * Main presenter
 */
@InjectConstructor
class MainPresenter(
    private val view: IMainView,
    private val serviceManager: ServicesManager,
    private val uiToServiceUC: UiToServiceUC,
    private val schedulers: ISchedulers
) : IMainPresenter {

    private val compositeDisposable = CompositeDisposable()
    private var askedPermissions: MutableList<SapPermission> = mutableListOf()


    override fun onCreated() {
        if (!view.checkSASupport()) {
            view.showNotSupported()
            view.showAddButton(false)
        } else {
            checkProviders()
            if (serviceManager.enabledServices.isEmpty()) {
                onAddService()
            }
        }
        subscribe()
    }

    private fun subscribe() {
        uiToServiceUC.onServiceAdded
            .observeOn(schedulers.mainThread)
            .subscribeBy(onNext = { checkProviders() })
            .addTo(compositeDisposable)

        uiToServiceUC.onServiceRemoved
            .observeOn(schedulers.mainThread)
            .subscribeBy(onNext = { checkProviders() })
            .addTo(compositeDisposable)
    }

    private fun checkProviders() {
        serviceManager.permissionServices
            .filter { x -> x.checked }
            .forEach { l ->
                l.permissions?.permissions?.forEach PermissionLoop@
                { p ->
                    if (!view.checkPermission(p) && p.required) {
                        view.showPermissionNotGrantedDialog(
                            l,
                            if (p.permissions.any { pp -> pp == android.Manifest.permission.ACCESS_FINE_LOCATION }) {
                                PermissionType.LOCATION
                            } else {
                                PermissionType.STORAGE
                            }
                        )
                    }
                }
            }

        if (serviceManager.enabledServices.isEmpty()) {
            view.showInvite()
        } else {
            view.showList()
        }

        view.showAddButton(serviceManager.availableProviders.isNotEmpty())
    }


    override fun onPermissionDialogCancel(permission: SapPermission) {
        askedPermissions.add(permission)

        if (permission.required) {
            onPermissionsNotGranted(permission.permissions)
            return
        }

        serviceManager.permissionServices
            .firstOrNull {
                it.permissions?.permissions?.any { p -> p == permission } == true
            }
            ?.name
            ?.let { permissionName -> askPermissions(permissionName) }
    }

    override fun onPermissionDialogOk(permission: SapPermission) {
        askedPermissions.add(permission)
        view.requestPermission(permission.permissions)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
    }

    override fun onPause() {
    }

    override fun onResume() {
    }


    private fun askPermissions(serviceName: String) {
        val service = serviceManager.permissionServices
            .firstOrNull { it.name == serviceName }
            ?: return

        val toAsk = service.permissions!!.permissions
            .subtract(askedPermissions.toSet())

        if (toAsk.isEmpty()) {
            return
        }


        toAsk.firstOrNull { it.description.isNotBlank() }
            ?.let {
                if (!view.checkPermission(it)) {
                    view.showPermissionDialog(it)
                    return
                }
            }
            ?: kotlin.run {
                toAsk.forEach {
                    if (!view.checkPermission(it)) {
                        view.requestPermission(toAsk.first().permissions)
                        return
                    }
                }
            }

        checkProviders()
    }

    override fun onServiceAdded(serviceName: String) {
        view.showList()
    }

    override fun onAddService() {
        view.showProvidersSelection()
    }

    override fun onServicesAdded(services: List<ServiceProxy>) {
        services.toObservable()
            .concatMapCompletable { uiToServiceUC(ServiceRequest.ServiceAdded(it)) }
            .subscribeBy(onError = Timber::e)
            .addTo(compositeDisposable)

        services
            .firstOrNull { it.permissions != null }
            ?.let { askPermissions(it.name) }
    }

    private fun removeService(service: ServiceProxy) {
        uiToServiceUC(ServiceRequest.ServiceRemoved(service))
            .observeOn(schedulers.mainThread)
            .subscribeBy(
                onComplete = {
                    if (serviceManager.enabledServices.isEmpty()) {
                        view.showInvite()
                    }
                    view.showAddButton(serviceManager.availableProviders.isNotEmpty())
                },
                onError = Timber::e
            )
            .addTo(compositeDisposable)
    }

    override fun onPermissionsNotGranted(permissions: List<String>) {
        checkProviders()
    }

    override fun onOpenLink(address: String?) {
        address?.let { view.openLink(it) }
        view.closeOpenLinkNotification()
    }

    override fun onGoToSettingsDialog(service: ServiceProxy) {
    }

    override fun onRemoveServiceDialog(service: ServiceProxy) {
        removeService(service)
    }
}