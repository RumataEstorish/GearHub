package gearsoftware.gearhub.servicesimpl.enotegear.protocol

import com.google.gson.annotations.SerializedName

enum class PacketType {
    @SerializedName("0")
    SYNC,
    @SerializedName("1")
    NO_DATA,
    @SerializedName("2")
    NOTE,
    @SerializedName("3")
    NOTEBOOK,
    @SerializedName("8")
    LINKED_NOTEBOOK,
    @SerializedName("4")
    TAG,
    @SerializedName("5")
    ERROR,
    @SerializedName("6")
    COUNT,
    @SerializedName("7")
    USER
}