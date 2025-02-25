package gearsoftware.gearhub.servicesimpl.transgear.data.repository

import gearsoftware.gearhub.servicesimpl.transgear.Direction
import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import gearsoftware.gearhub.servicesimpl.transgear.data.model.gear.FileTransfer

object GearMapper{
    fun toGear(source: FilesTransferRequest): FileTransfer =
            FileTransfer(name = source.name ?: "",
                    path = source.path ?: "",
                    size = source.size)

    fun fromGear(source: FileTransfer): FilesTransferRequest =
            FilesTransferRequest(
                    name = source.name,
                    path = source.path,
                    size = source.size
            ).apply { direction = Direction.DOWNLOAD }
}