package gearsoftware.gearhub.servicesimpl.transgear

import java.io.File

/**
 * FileTransfer request
 */
data class FilesTransferRequest(
        var id: Int = NO_TRANSFER,
        val name: String? = null,
        val path: String? = null,
        val size: Long = 0
) {
    companion object {
        const val NO_TRANSFER = -2
    }

    var direction: Direction = Direction.UPLOAD
    var isRingtone: Boolean = false

    @Transient
    var progress: Int = 0

    constructor(file: File, isRingtone: Boolean) : this(NO_TRANSFER, file.name, file.absolutePath, file.length()) {
        this.isRingtone = isRingtone
    }

    constructor(file: File) : this(NO_TRANSFER, file.name, file.absolutePath, file.length())
}
