package gearsoftware.gearhub.servicesimpl.transgear.data.repository

import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftCompleted
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftProgress
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftReceive
import io.reactivex.rxjava3.core.Observable

interface ISaftRepository {

    val onFileReceive: Observable<SaftReceive>
    val onProgress: Observable<SaftProgress>
    val onFilesCanceled: Observable<Unit>
    val onFileCompleted: Observable<SaftCompleted>

    fun send(file: FilesTransferRequest): Int
    fun send(files: List<FilesTransferRequest>): List<Int>
    fun cancel(id: Int)
    fun cancelAll()
}