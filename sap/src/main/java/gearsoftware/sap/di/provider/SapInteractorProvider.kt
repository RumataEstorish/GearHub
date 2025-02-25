package gearsoftware.sap.di.provider

import gearsoftware.sap.SapInteractor
import gearsoftware.sap.data.ISchedulers
import gearsoftware.sap.data.gearhttp.NetRequestRepository
import gearsoftware.sap.data.location.LocationMapper
import gearsoftware.sap.data.location.LocationRepository
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
internal class SapInteractorProvider(
        private val netRequestRepository: NetRequestRepository,
        private val locationRepository: LocationRepository,
        private val schedulers: ISchedulers,
        private val locationMapper: LocationMapper
) : Provider<SapInteractor> {
    override fun get(): SapInteractor =
            SapInteractor(netRequestRepository, locationRepository, schedulers, locationMapper)
}