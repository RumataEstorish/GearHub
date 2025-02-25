package gearsoftware.gearhub.servicesimpl.transgear.data.repository

import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest.Companion.NO_TRANSFER
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftCompleted
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftProgress
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftReceive
import gearsoftware.gearhub.servicesimpl.transgear.data.sources.ISAFTSource
import io.reactivex.rxjava3.core.Observable
import toothpick.InjectConstructor

@InjectConstructor
internal class SaftRepository(
        private val saftSource: ISAFTSource
) : ISaftRepository {

    override val onFileReceive: Observable<SaftReceive> =
            saftSource.onReceive

    override val onProgress: Observable<SaftProgress> =
            saftSource.onProgress

    override val onFilesCanceled: Observable<Unit> =
            saftSource.onCancelAll

    override val onFileCompleted: Observable<SaftCompleted> =
            saftSource.onTransferCompleted

    override fun send(file: FilesTransferRequest): Int {
        if (file.path.isNullOrBlank()) {
            return NO_TRANSFER
        }
        return saftSource.send(file.path)
    }

    override fun send(files: List<FilesTransferRequest>): List<Int> =
            files.map {
                if (!it.path.isNullOrBlank()) {
                    saftSource.send(it.path)
                } else {
                    NO_TRANSFER
                }
            }


    override fun cancel(id: Int) {
        saftSource.cancel(id)
    }

    override fun cancelAll() {
        saftSource.cancelAll()
    }
}