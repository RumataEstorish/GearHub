package gearsoftware.gearhub.view.servicelist

import gearsoftware.gearhub.serviceprovider.ServiceProxy


/**
 * Service presenter in list
 */
interface IServiceListPresenter {
    fun onServiceRemoveClick(service : ServiceProxy)
    fun onLoginClick(service: ServiceProxy)
    fun onLogoutClick(service: ServiceProxy)
    fun onSettingsClick(service: ServiceProxy)
    fun onPause()
    fun onResume()
}