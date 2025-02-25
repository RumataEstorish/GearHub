package gearsoftware.gearhub.servicesimpl.transgear.data.sources

import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest

interface IQueueSource {
    val isEmpty: Boolean

    fun add(files: List<FilesTransferRequest>)
    fun add(file: FilesTransferRequest)

    fun getByTransferId(id: Int): FilesTransferRequest?
    fun getByFileName(name: String): FilesTransferRequest?
    fun getByFilePath(path: String): FilesTransferRequest?
    fun getList(): List<FilesTransferRequest>

    fun contains(file: FilesTransferRequest): Boolean

    fun remove(filesTransferRequest: FilesTransferRequest?)
    fun removeByTransferId(id: Int)
    fun clear()
}