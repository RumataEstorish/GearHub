package gearsoftware.gearhub.view.servicelist

import gearsoftware.gearhub.serviceprovider.ServiceProxy

/**
 * Services list view
 */
interface IServiceListView {
    fun update(services: List<ServiceProxy>)
}