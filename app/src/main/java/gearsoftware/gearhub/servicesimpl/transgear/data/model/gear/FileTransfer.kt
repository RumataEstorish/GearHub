package gearsoftware.gearhub.servicesimpl.transgear.data.model.gear

import com.google.gson.annotations.SerializedName

data class FileTransfer(
        @SerializedName("name")
        val name: String = "",
        @SerializedName("path")
        val path: String = "",
        @SerializedName("size")
        val size: Long = 0)