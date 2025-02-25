package gearsoftware.gearhub.servicesimpl.transgear.data.sources

import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftCompleted
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftProgress
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftReceive
import io.reactivex.rxjava3.subjects.PublishSubject

interface ISAFTSource {
    val onProgress: PublishSubject<SaftProgress>
    val onReceive: PublishSubject<SaftReceive>
    val onCancelAll: PublishSubject<Unit>
    val onTransferCompleted: PublishSubject<SaftCompleted>

    fun send(filePath: String): Int
    fun cancel(id: Int)
    fun cancelAll()
}