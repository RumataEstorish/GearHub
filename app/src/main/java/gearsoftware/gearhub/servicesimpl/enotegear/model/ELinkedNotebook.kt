package gearsoftware.gearhub.servicesimpl.enotegear.model

import com.evernote.edam.type.LinkedNotebook
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class ELinkedNotebook(
        @Suppress("unused")
        @SerializedName("tempGuid")
        val tempGuid: String? = null
) : LinkedNotebook() {

    companion object {
        fun fromNotebook(notebook: LinkedNotebook): ELinkedNotebook =
                Gson().run {
                    fromJson(toJson(notebook), ELinkedNotebook::class.java)
                }
    }
}