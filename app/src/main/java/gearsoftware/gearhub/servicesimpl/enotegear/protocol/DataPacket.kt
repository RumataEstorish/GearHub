package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import com.google.gson.annotations.SerializedName
import gearsoftware.gearhub.servicesimpl.enotegear.SyncResult

abstract class DataPacket(packetType: PacketType) : BasePacket(packetType){

    @SerializedName("guid")
    var guid : String? = null

    @SerializedName("operation")
    var operation: Operation = Operation.CREATE

    abstract fun fromSyncResult(syncResult: SyncResult, index : Int) : BasePacket

    override fun fromSyncResult(syncResult: SyncResult): BasePacket {
        super.fromSyncResult(syncResult)
        return this
    }
}