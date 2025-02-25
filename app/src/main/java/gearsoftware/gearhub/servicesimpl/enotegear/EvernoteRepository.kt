package gearsoftware.gearhub.servicesimpl.enotegear

import com.evernote.client.android.EvernoteSession
import com.evernote.edam.error.EDAMSystemException
import com.evernote.edam.error.EDAMUserException
import com.evernote.edam.notestore.SyncChunk
import com.evernote.edam.notestore.SyncChunkFilter
import com.evernote.edam.type.*
import com.evernote.thrift.TException
import com.evernote.thrift.transport.TTransportException
import gearsoftware.gearhub.servicesimpl.enotegear.model.ENote
import gearsoftware.gearhub.servicesimpl.enotegear.model.ENotebook
import gearsoftware.gearhub.servicesimpl.enotegear.model.ETag
import gearsoftware.gearhub.servicesimpl.enotegear.model.SyncStatus
import gearsoftware.sap.data.ISchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import toothpick.InjectConstructor

@InjectConstructor
class EvernoteRepository(
        private val evernoteSession: EvernoteSession,
        private val schedulers: ISchedulers
) : IEvernoteRepository {

    private val evernoteClientFactory = evernoteSession.evernoteClientFactory

    private val syncStatusSubject: PublishSubject<SyncStatus> = PublishSubject.create()
    override val syncStatus: Observable<SyncStatus> =
        syncStatusSubject.hide()
            .subscribeOn(schedulers.io)


    override fun logout() {
        evernoteSession.logOut()
    }
    /*private fun getAllSharedNotes(): List<Note> {
        val noteFilter = NoteFilter()
        noteFilter.words = "sharedate:*"
        val sharedNotes = mutableListOf<Note>()
        var offset = 0

        while (sharedNotes.size < Int.MAX_VALUE) {
            try {
                val noteList = noteStoreClient.findNotes(noteFilter, offset, 50)
                sharedNotes.addAll(noteList.notes)
            } catch (e: EDAMNotFoundException) {
                Timber.e(e, "Error getting shared notes:")
                return emptyList()
            } catch (e: EDAMSystemException) {
                Timber.e(e, "Error getting shared notes:")
                return emptyList()
            } catch (e: EDAMUserException) {
                Timber.e(e, "Error getting shared notes:")
                return emptyList()
            } catch (e: TException) {
                Timber.e(e, "Error getting shared notes:")
                return emptyList()
            }

            if (sharedNotes.size % 50 != 0) {
                // We've retrieved all of the notes
                break
            } else {
                offset += 50
            }
        }
        return sharedNotes
    }*/


    override fun getUser(): Single<User> =
            Single.fromCallable {
                evernoteClientFactory.userStoreClient.user
            }
                    .subscribeOn(schedulers.io)


    override fun createTag(newTag: ETag): Single<ETag> =
            Single.fromCallable {
                with(evernoteClientFactory.noteStoreClient.createTag(newTag)) {
                    ETag.fromTag(this).apply {
                        this.tempGuid = newTag.tempGuid
                    }
                }
            }
                    .subscribeOn(schedulers.io)


    override fun deleteNote(guid: String): Single<Int> =
            Single.fromCallable {
                evernoteClientFactory.noteStoreClient.deleteNote(guid)
            }
                    .subscribeOn(schedulers.io)

    override fun getNoteContent(guid: String): Single<Note> =
            Single.fromCallable {
                evernoteClientFactory.noteStoreClient.getNote(guid, true, false, false, false)
            }
                    .subscribeOn(schedulers.io)

    override fun createNote(newNote: ENote): Single<ENote> =
            Single.fromCallable {
                with(evernoteClientFactory.noteStoreClient.createNote(newNote)) {
                    ENote.fromNote(this).apply { this.tempGuid = newNote.tempGuid }
                }
            }
                    .subscribeOn(schedulers.io)

    override fun updateNote(n: ENote): Single<Note> =
            Single.fromCallable { evernoteClientFactory.noteStoreClient.updateNote(n) }
                    .subscribeOn(schedulers.io)

    override fun getNotebooks(): Single<List<ENotebook>> =
            Single.fromCallable {
                var syncChunk: SyncChunk
                val filter = SyncChunkFilter().apply { isIncludeExpunged = false }
                val notebookList = mutableListOf<Notebook>()
                var localChunk = 0

                filter.isIncludeNotebooks = true
                filter.isIncludeLinkedNotebooks = true

                do {
                    syncChunk = evernoteClientFactory.noteStoreClient.getFilteredSyncChunk(localChunk, Integer.MAX_VALUE, filter)

                    if (syncChunk.isSetNotebooks) {
                        notebookList.addAll(syncChunk.notebooks)
                    }
                    localChunk = syncChunk.chunkHighUSN
                } while (syncChunk.isSetNotebooks || syncChunk.isSetExpungedNotebooks)

                notebookList.map { ENotebook.fromNotebook(it) }
            }
                    .subscribeOn(schedulers.io)

    override fun fullSync(withContent: Boolean): Single<SyncResult> =
            sync(0, withContent)

    override fun sync(usn: Int, withContent: Boolean): Single<SyncResult> =
            Single.fromCallable {
                syncStatusSubject.onNext(SyncStatus.NOTEBOOKS)

                var syncChunk: SyncChunk
                val filter = SyncChunkFilter().apply { isIncludeExpunged = true }
                val expungedTags = mutableListOf<String>()
                val tagsList = mutableListOf<Tag>()
                val notesList = mutableListOf<Note>()
                val expungedNotes = mutableListOf<String>()
                val notebookList = mutableListOf<Notebook>()
                val expungedNotebooks = mutableListOf<String>()
                val linkedNotebooks = mutableListOf<LinkedNotebook>()
                var usnChunk = 0
                var localChunk: Int

                try {

                    filter.isIncludeNotebooks = true
                    filter.isIncludeLinkedNotebooks = true

                    localChunk = usn
                    do {
                        syncChunk = evernoteClientFactory.noteStoreClient.getFilteredSyncChunk(localChunk, Integer.MAX_VALUE, filter)

                        if (syncChunk.isSetNotebooks) {
                            notebookList.addAll(syncChunk.notebooks)
                        }
                        if (syncChunk.isSetExpungedNotebooks) {
                            expungedNotebooks.addAll(syncChunk.expungedNotebooks)
                        }
                        if (syncChunk.isSetLinkedNotebooks) {
                            linkedNotebooks.addAll(syncChunk.linkedNotebooks)
                        }
                        if (syncChunk.chunkHighUSN > usnChunk) {
                            usnChunk = syncChunk.chunkHighUSN
                        }
                        localChunk = syncChunk.chunkHighUSN
                    } while (syncChunk.isSetNotebooks || syncChunk.isSetExpungedNotebooks)

                    filter.isIncludeNotebooks = false

                    localChunk = usn
                    filter.isIncludeTags = true

                    syncStatusSubject.onNext(SyncStatus.TAGS)

                    do {
                        syncChunk = evernoteClientFactory.noteStoreClient.getFilteredSyncChunk(localChunk, Integer.MAX_VALUE, filter)
                        if (syncChunk.isSetTags) {
                            tagsList.addAll(syncChunk.tags)
                        }
                        if (syncChunk.isSetExpungedTags) {
                            expungedTags.addAll(syncChunk.expungedTags)
                        }
                        if (syncChunk.chunkHighUSN > usnChunk) {
                            usnChunk = syncChunk.chunkHighUSN
                        }
                        localChunk = syncChunk.chunkHighUSN
                    } while (syncChunk.isSetTags || syncChunk.isSetExpungedTags)
                    filter.isIncludeTags = false

                    localChunk = usn

                    filter.isIncludeNotes = true

                    syncStatusSubject.onNext(SyncStatus.NOTES)
                    do {
                        syncChunk = evernoteClientFactory.noteStoreClient.getFilteredSyncChunk(localChunk, Integer.MAX_VALUE, filter)
                        if (syncChunk.isSetNotes) {
                            if (withContent) {
                                notesList.addAll(syncChunk.notes.map {
                                    getNoteContent(it.guid).blockingGet()
                                })
                            } else {
                                notesList.addAll(syncChunk.notes)
                            }
                        }
                        if (syncChunk.isSetExpungedNotes) {
                            expungedNotes.addAll(syncChunk.expungedNotes)
                        }
                        if (syncChunk.chunkHighUSN > usnChunk) {
                            usnChunk = syncChunk.chunkHighUSN
                        }
                        localChunk = syncChunk.chunkHighUSN
                    } while (syncChunk.isSetNotes || syncChunk.isSetExpungedNotes)

                    // notesList.addAll(getAllSharedNotes())

                    SyncResult(
                            usnChunk,
                            notebookList,
                            linkedNotebooks,
                            notesList,
                            tagsList,
                            expungedNotebooks,
                            expungedNotes,
                            expungedTags
                    )
                } catch (e1: EDAMUserException) {
                    Timber.e(e1)
                    SyncResult(usnChunk, e1.localizedMessage)
                } catch (e1: EDAMSystemException) {
                    Timber.e(e1)
                    SyncResult(usnChunk, e1.localizedMessage)
                } catch (e1: TTransportException) {
                    SyncResult(usnChunk, e1.localizedMessage)
                } catch (e1: TException) {
                    Timber.e(e1)
                    SyncResult(usnChunk, e1.localizedMessage)
                }
            }
                    .doOnSuccess { syncStatusSubject.onNext(SyncStatus.IDLE) }
                    .subscribeOn(schedulers.io)
}