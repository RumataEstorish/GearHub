package gearsoftware.gearhub.di.providers

import gearsoftware.gearhub.di.InternalServices
import gearsoftware.gearhub.services.data.ServiceList
import gearsoftware.gearhub.services.data.sources.ServicesInternalSource
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
class ServicesInternalSourceProvider (
        @InternalServices
        private val serviceList: ServiceList
) : Provider<ServicesInternalSource> {
    override fun get(): ServicesInternalSource =
            ServicesInternalSource(serviceList)
}