package gearsoftware.gearhub.servicesimpl.transgear.data.repository

import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import gearsoftware.gearhub.servicesimpl.transgear.data.model.gear.FileTransfer

interface IQueueRepository {
    /*Queue*/
    fun clear()

    fun getAll(): List<FilesTransferRequest>
    fun hasFiles(): Boolean
    fun getById(id: Int): FilesTransferRequest?
    fun getByPath(path: String): FilesTransferRequest?
    fun add(files: List<FilesTransferRequest>)
    fun add(file: FilesTransferRequest)
    fun remove(file: FilesTransferRequest)
    fun remove(file: FileTransfer)
    fun contains(file: FilesTransferRequest): Boolean
    fun removeByTransferId(id: Int)
}
