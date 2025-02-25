package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import com.google.gson.annotations.SerializedName
import gearsoftware.gearhub.servicesimpl.enotegear.SyncResult
import gearsoftware.gearhub.servicesimpl.enotegear.model.ENotebook

data class NotebookPacket(
        @SerializedName("notebook")
        var notebook: ENotebook? = null
) : DataPacket(PacketType.NOTEBOOK) {

    override fun fromSyncResult(syncResult: SyncResult, index: Int): BasePacket {
        super.fromSyncResult(syncResult)
        notebook = ENotebook.fromNotebook(syncResult.notebooks[index])
        guid = notebook?.guid
        return this
    }

}