package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import com.evernote.edam.type.User
import com.google.gson.annotations.SerializedName
import gearsoftware.gearhub.servicesimpl.enotegear.SyncResult

data class UserPacket(
        @SerializedName("user")
        val user: User
) : DataPacket(PacketType.USER) {

    override fun fromSyncResult(syncResult: SyncResult, index: Int): BasePacket {
        super.fromSyncResult(syncResult)
        return this
    }
}