package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import com.google.gson.annotations.SerializedName

class ErrorPacket(
        usn: Int,
        @Suppress("unused")
        @SerializedName("error_message")
        val errorMessage: String? = null
) : BasePacket(PacketType.ERROR) {

    init {
        super.usn = usn
    }

    constructor(usn: Int, error: Throwable) : this(usn, error.toString())
}