package gearsoftware.gearhub.services.data

import gearsoftware.gearhub.serviceprovider.ServiceProxy

class ServiceList(
    vararg pairs: Pair<String, ServiceProxy>
) : LinkedHashMap<String, ServiceProxy>(pairs.size) {
    init {
        putAll(pairs)
    }
}