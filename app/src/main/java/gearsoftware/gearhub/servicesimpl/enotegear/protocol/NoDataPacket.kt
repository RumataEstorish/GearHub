package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import gearsoftware.gearhub.servicesimpl.enotegear.SyncResult

class NoDataPacket(usn: Int = 0) : BasePacket(PacketType.NO_DATA) {

    init {
        super.usn = usn
    }

    override fun fromSyncResult(syncResult: SyncResult): BasePacket {
        super.fromSyncResult(syncResult)
        return this
    }
}