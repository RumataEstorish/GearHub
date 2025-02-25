package gearsoftware.gearhub.servicesimpl.enotegear

import com.evernote.edam.type.LinkedNotebook
import com.evernote.edam.type.Note
import com.evernote.edam.type.Notebook
import com.evernote.edam.type.Tag

/**
 * Synchronization result
 */
data class SyncResult(
        val usn: Int = 0,
        val notebooks: List<Notebook> = emptyList(),
        val linkedNotebooks: List<LinkedNotebook> = emptyList(),
        val notes: List<Note> = emptyList(),
        val tags: List<Tag> = emptyList(),
        val expungedNotebooks: List<String> = emptyList(),
        val expungedNotes: List<String> = emptyList(),
        val expungedTags: List<String> = emptyList()
) {

    constructor(usn: Int, error: String?) : this(usn, listOf(), listOf(), listOf(), listOf(), listOf(), listOf()) {
        this.error = error
    }

    var error: String? = null
        private set

    fun isEmpty(): Boolean =
            expungedNotes.isEmpty() && expungedNotebooks.isEmpty() && expungedTags.isEmpty()
                    && notebooks.isEmpty() && notes.isEmpty() && tags.isEmpty()
}
