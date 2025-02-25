package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import com.google.gson.annotations.SerializedName
import gearsoftware.gearhub.servicesimpl.enotegear.SyncResult
import gearsoftware.gearhub.servicesimpl.enotegear.model.ELinkedNotebook

data class LinkedNotebookPacket(
        @SerializedName("linkedNotebook")
        var linkedNotebook: ELinkedNotebook? = null
) : DataPacket(PacketType.LINKED_NOTEBOOK) {

    override fun fromSyncResult(syncResult: SyncResult, index: Int): BasePacket {
        super.fromSyncResult(syncResult)
        linkedNotebook = ELinkedNotebook.fromNotebook(syncResult.linkedNotebooks[index])
        guid = linkedNotebook?.guid
        return this
    }

}