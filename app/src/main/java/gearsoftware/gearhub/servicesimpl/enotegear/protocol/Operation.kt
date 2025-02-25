package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import com.google.gson.annotations.SerializedName

enum class Operation {
    @SerializedName("0")
    CREATE,
    @SerializedName("1")
    UPDATE,
    @SerializedName("2")
    DELETE,
    @SerializedName("3")
    CONTENT,
}