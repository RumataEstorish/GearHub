package gearsoftware.gearhub.servicesimpl.transgear

import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest.Companion.NO_TRANSFER
import gearsoftware.gearhub.servicesimpl.transgear.data.model.gear.FileTransfer
import gearsoftware.gearhub.servicesimpl.transgear.data.repository.GearMapper
import gearsoftware.gearhub.servicesimpl.transgear.data.repository.IQueueRepository
import gearsoftware.gearhub.servicesimpl.transgear.data.repository.ISaftRepository
import gearsoftware.sap.ISap
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import toothpick.InjectConstructor
import java.io.File

@InjectConstructor
class TransGearInteractor(
        private val saftRepository: ISaftRepository,
        private val queueRepository: IQueueRepository,
        private val sap: ISap) {

    var currentTransfer: Int = NO_TRANSFER
        private set

    val onQueueChanged: PublishSubject<List<FilesTransferRequest>> = PublishSubject.create()

    val onProgress: Observable<FilesTransferRequest> = saftRepository.onProgress
            .doOnNext { currentTransfer = it.id }
            .map {
                (queueRepository.getById(it.id)
                        ?: FilesTransferRequest(it.id)).apply { progress = it.progress }
            }


    val onTransferCompleted: Observable<FilesTransferRequest> = saftRepository.onFileCompleted
            .map {
                queueRepository.getById(it.id)?.apply { progress = 100 }
                        ?: FilesTransferRequest(it.id).apply { progress = 100 }
            }
            .doOnNext {
                currentTransfer = NO_TRANSFER
                queueRepository.remove(it)
                onQueueChanged.onNext(filesList)
            }

    val currentTransferFile: FilesTransferRequest?
        get() = queueRepository.getById(currentTransfer)

    /*val hasTransferFiles: Boolean
        get() = queueRepository.hasFiles()*/

    val filesList: List<FilesTransferRequest>
        get() = queueRepository.getAll()


    fun cancelCurrentTransfer() {
        if (currentTransfer != NO_TRANSFER) {
            saftRepository.cancel(currentTransfer)
            queueRepository.removeByTransferId(currentTransfer)
        }
        currentTransfer = NO_TRANSFER
        onQueueChanged.onNext(emptyList())
    }

    fun transferFiles(files: Array<File>, isRingtone: Boolean) {
        transferFiles(files.map {
            queueRepository.getByPath(it.absolutePath) ?: FilesTransferRequest(it)
        }, isRingtone)
    }

    fun transferFiles(files: List<FilesTransferRequest>, isRingtone: Boolean) {
        if (files.isEmpty()) {
            return
        }

        files.forEach {
            if (!queueRepository.contains(it)) {
                queueRepository.add(it)
            }
        }

        if (!sap.isConnected) {
            sap.startConnectToWatch()
        } else {

            files.forEach { it.id = saftRepository.send(it) }
            /*val f = files.filter { !it.path.isNullOrBlank() && !queueRepository.contains(it) }
            if (f.isEmpty()) {
                return
            }*/
            onQueueChanged.onNext(files)
        }
    }

    fun onCancel(fileTransfer: FileTransfer) {
        if (filesList.isEmpty()) {
            sap.stopConnectToWatch()
        }
        queueRepository.remove(fileTransfer)
        onQueueChanged.onNext(filesList)
    }

    fun cancelAllTransfer() {
        sap.stopConnectToWatch()
        saftRepository.cancelAll()
        queueRepository.clear()
        onQueueChanged.onNext(emptyList())
        currentTransfer = NO_TRANSFER
    }

    fun onReceiveFilesList(fileTransfer: Array<FileTransfer>) {
        queueRepository.add(fileTransfer.map { GearMapper.fromGear(it) })
        onQueueChanged.onNext(filesList)
    }

    fun onConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            return
        }

        val files = queueRepository.getAll().filter { it.id == NO_TRANSFER }

        transferFiles(files.filter { it.isRingtone }, true)
        transferFiles(files.filter { !it.isRingtone }, false)
    }
}