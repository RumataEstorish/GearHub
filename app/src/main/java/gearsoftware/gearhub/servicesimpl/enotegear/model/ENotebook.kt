package gearsoftware.gearhub.servicesimpl.enotegear.model

import com.evernote.edam.type.Notebook
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class ENotebook(
        @Suppress("unused")
        @SerializedName("tempGuid")
        val tempGuid: String? = null
) : Notebook() {
    companion object {
        fun fromNotebook(notebook: Notebook): ENotebook =
                Gson().run {
                    fromJson(toJson(notebook), ENotebook::class.java)
                }
    }
}