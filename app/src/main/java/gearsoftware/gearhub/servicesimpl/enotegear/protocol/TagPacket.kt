package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import com.evernote.edam.type.Tag
import com.google.gson.annotations.SerializedName
import gearsoftware.gearhub.servicesimpl.enotegear.SyncResult
import gearsoftware.gearhub.servicesimpl.enotegear.model.ETag

data class TagPacket(
        @SerializedName("tag")
        var tag: ETag? = null
) : DataPacket(PacketType.TAG) {

    constructor(operation: Operation) : this() {
        this.operation = operation
    }

    constructor(operation: Operation, usn: Int) : this(operation) {
        this.usn = usn
    }

    constructor(operation: Operation, usn: Int, tag: Tag?) : this(operation, usn) {
        this.usn = usn
        if (tag != null) {
            this.tag = ETag.fromTag(tag)
        }
    }

    override fun fromSyncResult(syncResult: SyncResult, index: Int): BasePacket {
        super.fromSyncResult(syncResult)
        tag = ETag.fromTag(syncResult.tags[index])
        guid = tag?.guid
        return this
    }

}