package gearsoftware.gearhub.servicesimpl.transgear.data.repository

import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import gearsoftware.gearhub.servicesimpl.transgear.data.model.gear.FileTransfer
import gearsoftware.gearhub.servicesimpl.transgear.data.sources.IQueueSource
import toothpick.InjectConstructor

@InjectConstructor
internal class QueueRepository(
        private val queueSource: IQueueSource,
        private val gearMapper: GearMapper
) : IQueueRepository {

    override fun getAll(): List<FilesTransferRequest> =
            queueSource.getList()

    override fun hasFiles(): Boolean =
            !queueSource.isEmpty

    override fun getById(id: Int): FilesTransferRequest? =
            queueSource.getByTransferId(id)

    override fun add(files: List<FilesTransferRequest>) =
            queueSource.add(files)

    override fun add(file: FilesTransferRequest) =
            queueSource.add(file)

    override fun clear() =
            queueSource.clear()

    override fun removeByTransferId(id: Int) =
            queueSource.removeByTransferId(id)

    override fun remove(file: FilesTransferRequest) =
            queueSource.remove(file)

    override fun remove(file: FileTransfer) =
            remove(gearMapper.fromGear(file))

    override fun contains(file: FilesTransferRequest): Boolean =
            queueSource.contains(file)

    override fun getByPath(path: String): FilesTransferRequest? =
            queueSource.getByFilePath(path)
}