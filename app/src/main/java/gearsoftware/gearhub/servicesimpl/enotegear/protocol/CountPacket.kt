package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import com.google.gson.annotations.SerializedName
import gearsoftware.gearhub.servicesimpl.enotegear.SyncResult


data class CountPacket(
        @SerializedName("notesCount")
        var notesCount: Int? = null,

        @SerializedName("notebooksCount")
        var notebooksCount: Int? = null,

        @SerializedName("tagsCount")
        var tagsCount: Int? = null

) : BasePacket(PacketType.COUNT) {

    override fun fromSyncResult(syncResult: SyncResult): BasePacket {
        super.fromSyncResult(syncResult)
        notesCount = syncResult.notes.size
        notebooksCount = syncResult.notebooks.size
        tagsCount = syncResult.tags.size
        return this
    }
}