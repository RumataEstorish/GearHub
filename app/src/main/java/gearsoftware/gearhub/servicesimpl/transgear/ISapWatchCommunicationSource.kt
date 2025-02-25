package gearsoftware.gearhub.servicesimpl.transgear

import gearsoftware.gearhub.servicesimpl.transgear.data.model.gear.FileTransfer
import gearsoftware.sap.ISap

interface ISapWatchCommunicationSource : ISap {
    fun sendFilesListToWatch(files: List<FileTransfer>)
}