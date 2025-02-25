@file:Suppress("unused")

package gearsoftware.gearhub.servicesimpl.enotegear

import com.evernote.client.android.EvernoteSession
import com.evernote.edam.type.User
import com.google.gson.Gson
import gearsoftware.gearhub.servicesimpl.enotegear.ENoteGearServiceProvider.Companion.FULL_SYNC_CHANNEL_ID
import gearsoftware.gearhub.servicesimpl.enotegear.model.ENotebook
import gearsoftware.gearhub.servicesimpl.enotegear.model.SyncStatus
import gearsoftware.gearhub.servicesimpl.enotegear.protocol.*
import gearsoftware.gearhub.servicesimpl.enotegear.protocol.Operation.*
import gearsoftware.sap.data.model.WatchText
import gearsoftware.sap.utils.safeOnComplete
import gearsoftware.sap.utils.safeOnNext
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.InjectConstructor

@InjectConstructor
class ENoteGearProcessor(
    private val evernoteReposotiry: IEvernoteRepository,
    private val settings: Settings
) {

    val isLoggedIn: Boolean
        get() = EvernoteSession.getInstance().isLoggedIn

    val syncStatus: Observable<SyncStatus> =
        evernoteReposotiry.syncStatus

    private fun <T : DataPacket> sendArray(packet: Class<T>, syncResult: SyncResult, max: Int): List<BasePacket> {
        var outPack: BasePacket
        val list = mutableListOf<BasePacket>()

        for (i in 0 until max) {
            outPack = packet.newInstance().fromSyncResult(syncResult, i)
            list.add(outPack)
        }

        return list
    }

    private fun processSyncResult(syncResult: SyncResult): Observable<BasePacket> =
        Observable.create { emitter ->
            if (syncResult.error != null) {
                emitter.safeOnNext(ErrorPacket(syncResult.usn, syncResult.error))
                emitter.safeOnComplete()
                return@create
            }

            if (syncResult.isEmpty()) {
                emitter.safeOnNext(NoDataPacket(syncResult.usn))
                emitter.safeOnComplete()
                return@create
            }

            emitter.safeOnNext(CountPacket().fromSyncResult(syncResult))
            emitter.safeOnNext(SyncPacket().fromSyncResult(syncResult))

            sendArray(NotePacket::class.java, syncResult, syncResult.notes.size)
                .forEach(emitter::safeOnNext)

            sendArray(NotebookPacket::class.java, syncResult, syncResult.notebooks.size)
                .forEach(emitter::safeOnNext)

            sendArray(LinkedNotebookPacket::class.java, syncResult, syncResult.linkedNotebooks.size)
                .forEach(emitter::safeOnNext)

            sendArray(TagPacket::class.java, syncResult, syncResult.tags.size)
                .forEach(emitter::safeOnNext)

            emitter.safeOnComplete()
        }

    fun getUser(): Single<User> =
        evernoteReposotiry.getUser()

    @Suppress("unused")
    fun getNotebooks(): Single<List<ENotebook>> =
        evernoteReposotiry.getNotebooks()

    fun process(str: String): Observable<WatchText> =
        Observable.create { emitter: ObservableEmitter<BasePacket> ->
            val packet: BasePacket
            val gson = Gson()

            try {
                packet = gson.fromJson(str, BasePacket::class.java)
            } catch (e: Exception) {
                Timber.e("ENoteGear gson parse error: $str")
                emitter.safeOnComplete()
                return@create
            }

            when (packet.type) {
                PacketType.SYNC ->
                    evernoteReposotiry.sync(packet.usn, settings.syncContent)
                        .flatMapObservable(::processSyncResult)
                        .subscribeBy(
                            onNext = emitter::safeOnNext,
                            onError = Timber::e,
                            onComplete = emitter::safeOnComplete
                        )
                PacketType.NOTE -> {
                    val notePacket = gson.fromJson(str, NotePacket::class.java)
                    when (notePacket.operation) {
                        CONTENT -> {
                            evernoteReposotiry.getNoteContent(notePacket.guid!!)
                                .subscribeBy(
                                    onSuccess = { result ->
                                        emitter.safeOnNext(NotePacket(CONTENT, result.updateSequenceNum, result))
                                        emitter.safeOnComplete()
                                    },
                                    onError = {
                                        emitter.onNext(ErrorPacket(packet.usn, it))
                                        emitter.safeOnComplete()
                                    })
                        }
                        CREATE -> {
                            evernoteReposotiry.createNote(notePacket.note!!)
                                .subscribeBy(
                                    onSuccess = { result ->
                                        emitter.safeOnNext(NotePacket(CREATE, result.updateSequenceNum, result))
                                        emitter.safeOnComplete()
                                    },
                                    onError = {
                                        emitter.safeOnNext(ErrorPacket(packet.usn, it))
                                        emitter.safeOnComplete()
                                    })
                        }
                        DELETE -> {
                            evernoteReposotiry.deleteNote(notePacket.guid!!)
                                .subscribeBy(
                                    onSuccess = { result ->
                                        emitter.safeOnNext(NotePacket(DELETE, result, null).apply { guid = notePacket.guid })
                                        emitter.safeOnComplete()
                                    },
                                    onError = {
                                        emitter.safeOnNext(ErrorPacket(packet.usn, it))
                                        emitter.safeOnComplete()
                                    })
                        }
                        UPDATE -> {
                            evernoteReposotiry.updateNote(notePacket.note!!)
                                .subscribeBy(
                                    onSuccess = { note ->
                                        emitter.safeOnNext(NotePacket(UPDATE, note.updateSequenceNum, note))
                                        emitter.safeOnComplete()
                                    },
                                    onError = {
                                        emitter.safeOnNext(ErrorPacket(packet.usn, it))
                                        emitter.safeOnComplete()
                                    })
                        }
                    }
                }
                PacketType.TAG -> {
                    val tagPacket = gson.fromJson(str, TagPacket::class.java)
                    when (tagPacket.operation) {
                        CREATE -> {
                            evernoteReposotiry.createTag(tagPacket.tag!!)
                                .subscribeBy(
                                    onSuccess = { result ->
                                        emitter.safeOnNext(TagPacket(CREATE, result.updateSequenceNum, result))
                                        emitter.safeOnComplete()
                                    },
                                    onError = {
                                        emitter.safeOnNext(ErrorPacket(packet.usn, it))
                                        emitter.safeOnComplete()
                                    })
                        }
                        else -> Unit
                    }
                }
                PacketType.USER -> {
                    evernoteReposotiry.getUser()
                        .subscribeBy(
                            onSuccess = { user ->
                                emitter.safeOnNext(UserPacket(user))
                                emitter.safeOnComplete()
                            },
                            onError = {
                                emitter.safeOnNext(ErrorPacket(packet.usn, it))
                                emitter.safeOnComplete()
                            }
                        )
                }
                else -> Unit
            }
        }
            .map { WatchText(FULL_SYNC_CHANNEL_ID, it.serialize()) }

    fun logout() {
        evernoteReposotiry.logout()
    }
}