package gearsoftware.gearhub.servicesimpl.enotegear

import com.evernote.edam.type.*
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import gearsoftware.gearhub.servicesimpl.enotegear.ENoteGearServiceProvider.Companion.FULL_SYNC_CHANNEL_ID
import gearsoftware.gearhub.servicesimpl.enotegear.model.ENote
import gearsoftware.gearhub.servicesimpl.enotegear.model.ETag
import gearsoftware.gearhub.servicesimpl.enotegear.protocol.*
import gearsoftware.gearhub.servicesimpl.enotegear.protocol.Operation.*
import gearsoftware.gearhub.servicesimpl.enotegear.protocol.PacketType.USER
import gearsoftware.sap.data.model.WatchText
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class ENoteGearProcessorTest {

    private val repository: IEvernoteRepository = mock()
    private val settings: Settings = mock()
    private val processor: ENoteGearProcessor = ENoteGearProcessor(repository, settings)

    @Before
    fun prepare() {
        whenever(settings.syncContent).doReturn(false)
    }

    @Test
    fun processInvalidString() {
        processor.process("SOME_RANDOM_STRING")
                .test()
                .assertComplete()
                .dispose()
    }

    @Test
    fun processGetUser() {
        val user = User()
        whenever(repository.getUser()).doReturn(Single.just(user))

        processor.process(BasePacket(USER).serialize())
                .test()
                .await()
                .assertResult(WatchText(FULL_SYNC_CHANNEL_ID, UserPacket(user).serialize()))
                .dispose()
    }

    @Test
    fun processCreateTag() {
        val newTag = ETag().apply { tempGuid = "TEMP_GUID" }
        val tagResult = ETag.fromTag(Tag().apply {
            guid = "NEW_GUID"
            updateSequenceNum = 12345
        }).apply {
            tempGuid = newTag.tempGuid
        }

        whenever(repository.createTag(newTag)).doReturn(Single.just(tagResult))

        processor.process(TagPacket(CREATE, 0, newTag).serialize())
                .test()
                .await()
                .assertResult(WatchText(FULL_SYNC_CHANNEL_ID, TagPacket(CREATE, 12345, tagResult).serialize()))
                .dispose()
    }

    @Test
    fun processUpdateNote() {
        val note = ENote()
        val noteResult = Note().apply { updateSequenceNum = 1234 }

        whenever(repository.updateNote(note)).doReturn(Single.just(noteResult))

        processor.process(NotePacket(UPDATE, 222, note).serialize())
                .test()
                .await()
                .assertResult(WatchText(FULL_SYNC_CHANNEL_ID, NotePacket(UPDATE, 1234, noteResult).serialize()))
                .dispose()
    }

    @Test
    fun processDeleteNote() {
        val note = ENote().apply { guid = "GUID" }

        whenever(repository.deleteNote(note.guid)).doReturn(Single.just(1234))

        processor.process(NotePacket(DELETE, 1111, note).apply { guid = "GUID" }.serialize())
                .test()
                .await()
                .assertResult(WatchText(FULL_SYNC_CHANNEL_ID, NotePacket(DELETE, 1234, null).apply { guid = note.guid }.serialize()))
                .dispose()
    }

    @Test
    fun processCreateNote() {
        val note = ENote().apply {
            content = "Test"
            tempGuid = "TEMP_GUID"
        }
        val noteResult = ENote.fromNote(Note()).apply {
            tempGuid = note.guid
            updateSequenceNum = 222
        }

        whenever(repository.createNote(note)).doReturn(Single.just(noteResult))

        processor.process(NotePacket(CREATE, 111, note).serialize())
                .test()
                .await()
                .assertResult(WatchText(FULL_SYNC_CHANNEL_ID, NotePacket(CREATE, 222, noteResult).serialize()))
                .dispose()
    }

    @Test
    fun procesGetContent() {
        val note = ENote().apply { guid = "GUID" }
        val noteResult = Note().apply {
            guid = note.guid
            content = "Test"
            updateSequenceNum = 222
        }

        whenever(repository.getNoteContent(note.guid)).doReturn(Single.just(noteResult))

        processor.process(NotePacket(CONTENT, 111, note).apply { guid = note.guid }.serialize())
                .test()
                .await()
                .assertResult(WatchText(FULL_SYNC_CHANNEL_ID, NotePacket(CONTENT, noteResult.updateSequenceNum, noteResult).serialize()))
                .dispose()
    }

    @Ignore("todo")
    @Test
    fun processSync() {

        val fullDataSyncResult = SyncResult(
                555,
                listOf(Notebook().apply { guid = "3" }, Notebook().apply { guid = "4" }),
                listOf(LinkedNotebook().apply { guid = "10" }),
                listOf(Note().apply { guid = "1" }, Note().apply { guid = "2" }),
                listOf(Tag().apply { guid = "4" }, Tag().apply { guid = "5" }),
                listOf("6", "7"),
                listOf("1", "2"),
                listOf("1", "4")
        )

        whenever(repository.sync(10)).doReturn(Single.just(fullDataSyncResult))

        processor.process(BasePacket(PacketType.SYNC).serialize())
                .test()
                .await()
                //testObserver.assertValues()
                .dispose()
    }
}