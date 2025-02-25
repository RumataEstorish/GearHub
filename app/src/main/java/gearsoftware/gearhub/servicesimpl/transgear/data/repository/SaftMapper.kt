package gearsoftware.gearhub.servicesimpl.transgear.data.repository

import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftReceive

object SaftMapper : ISaftMapper {
    override fun fromSaft(source: SaftReceive): FilesTransferRequest =
            FilesTransferRequest(
                    id = source.id,
                    name = source.name,
                    path = source.path)
}