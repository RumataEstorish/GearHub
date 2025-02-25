package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import gearsoftware.gearhub.servicesimpl.enotegear.SyncResult

open class BasePacket(
        @SerializedName("type")
        val type: PacketType = PacketType.SYNC
) {
    @SerializedName("usn")
    var usn: Int = 0

    open fun fromSyncResult(syncResult: SyncResult): BasePacket {
        usn = syncResult.usn
        return this
    }

    fun serialize(): String =
            Gson().toJson(this)
}