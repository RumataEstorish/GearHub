package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import com.google.gson.annotations.SerializedName
import gearsoftware.gearhub.servicesimpl.enotegear.SyncResult

data class SyncPacket(
        @SerializedName("expungedNotebooks")
        var expungedNotebooks: List<String> = emptyList(),
        @SerializedName("expungedNotes")
        var expungedNotes: List<String> = emptyList(),
        @SerializedName("expungedTags")
        var expungedTags: List<String> = emptyList()
) : BasePacket(PacketType.SYNC) {

    override fun fromSyncResult(syncResult: SyncResult): BasePacket {
        super.fromSyncResult(syncResult)
        expungedNotebooks = syncResult.expungedNotebooks
        expungedNotes = syncResult.expungedNotes
        expungedTags = syncResult.expungedTags
        return this
    }
}