package gearsoftware.gearhub.servicesimpl.enotegear.model

import com.evernote.edam.type.Note
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * Extension of Evernote's note for transfer needs
 */
data class ENote(
        @SerializedName("tempGuid")
        var tempGuid: String? = null
) : Note() {
    companion object {
        fun fromNote(note: Note): ENote =
                Gson().run {
                    fromJson(toJson(note), ENote::class.java)
                }
    }
}
