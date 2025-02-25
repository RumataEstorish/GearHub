package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import com.evernote.edam.type.Note
import com.google.gson.annotations.SerializedName
import gearsoftware.gearhub.servicesimpl.enotegear.SyncResult
import gearsoftware.gearhub.servicesimpl.enotegear.model.ENote

data class NotePacket(
        @SerializedName("note")
        var note: ENote? = null
) : DataPacket(PacketType.NOTE) {

    constructor(operation: Operation) : this() {
        this.operation = operation
    }

    constructor(operation: Operation, usn: Int) : this(operation) {
        this.usn = usn
    }

    constructor(operation: Operation, usn: Int, note: Note?) : this(operation, usn) {
        this.usn = usn
        if (note != null) {
            this.note = ENote.fromNote(note)
        }
    }

    override fun fromSyncResult(syncResult: SyncResult, index: Int): BasePacket {
        super.fromSyncResult(syncResult)
        note = ENote.fromNote(syncResult.notes[index])
        guid = note?.guid
        return this
    }
}