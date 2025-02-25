package gearsoftware.gearhub.servicesimpl.enotegear.model

import com.evernote.edam.type.Tag
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * Tag extension for Evernote's tag transfer needs
 */
data class ETag(
        @SerializedName("tempGuid")
        var tempGuid: String? = null
) : Tag() {
    companion object {
        fun fromTag(tag: Tag): ETag =
                Gson().run {
                    fromJson(toJson(tag), ETag::class.java)
                }
    }
}