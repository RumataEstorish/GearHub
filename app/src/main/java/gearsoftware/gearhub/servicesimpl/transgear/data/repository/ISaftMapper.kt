package gearsoftware.gearhub.servicesimpl.transgear.data.repository

import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftReceive

interface ISaftMapper {
    fun fromSaft(source: SaftReceive): FilesTransferRequest
}