package gearsoftware.gearhub.servicesimpl.enotegear

import com.evernote.edam.type.Note
import com.evernote.edam.type.User
import gearsoftware.gearhub.servicesimpl.enotegear.model.ENote
import gearsoftware.gearhub.servicesimpl.enotegear.model.ENotebook
import gearsoftware.gearhub.servicesimpl.enotegear.model.ETag
import gearsoftware.gearhub.servicesimpl.enotegear.model.SyncStatus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface IEvernoteRepository {
    val syncStatus: Observable<SyncStatus>

    fun getUser(): Single<User>
    fun createTag(newTag: ETag): Single<ETag>
    fun deleteNote(guid: String): Single<Int>
    fun getNoteContent(guid: String): Single<Note>
    fun createNote(newNote: ENote): Single<ENote>
    fun updateNote(n: ENote): Single<Note>
    fun fullSync(withContent: Boolean = false): Single<SyncResult>
    fun sync(usn: Int, withContent: Boolean = false): Single<SyncResult>
    fun getNotebooks(): Single<List<ENotebook>>
    fun logout()
}
