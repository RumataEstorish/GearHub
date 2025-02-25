package gearsoftware.gearhub.servicesimpl.transgear.data.sources

import android.content.SharedPreferences
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.*
import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class QueueSourceTest {

    private lateinit var queueSource: QueueSource
    private val prefs: SharedPreferences = mock()
    private val editor: SharedPreferences.Editor = mock()

    @Before
    fun create() {

        whenever(prefs.getString(any(), anyOrNull())).thenReturn("")
        whenever(prefs.edit()).thenReturn(editor)
        whenever(editor.putString(any(), anyOrNull())).thenReturn(editor)
        whenever(editor.remove(any())).thenReturn(editor)

        queueSource = QueueSource(prefs)
    }

    @Test
    fun isEmptyTest() {
        val filesTransferRequest = FilesTransferRequest(10)
        Assert.assertTrue(queueSource.isEmpty)
        queueSource.add(filesTransferRequest)
        Assert.assertFalse(queueSource.isEmpty)
    }

    @Test
    fun removeTest() {
        val filesTransferRequest = FilesTransferRequest(10)
        queueSource.remove(filesTransferRequest)
        queueSource.add(filesTransferRequest)
        Assert.assertFalse(queueSource.isEmpty)
        queueSource.remove(filesTransferRequest)
        Assert.assertTrue(queueSource.isEmpty)
    }

    @Test
    fun removeByIdTest() {
        val file = FilesTransferRequest(10)
        queueSource.removeByTransferId(100)
        queueSource.add(file)
        queueSource.removeByTransferId(file.id)
        Assert.assertTrue(queueSource.isEmpty)
    }

    @Test
    fun getByTransferIdTest() {
        val file = FilesTransferRequest(10)
        queueSource.add(file)
        Assert.assertNull(queueSource.getByTransferId(100))
        Assert.assertEquals(file, queueSource.getByTransferId(file.id))
    }

    @Test
    fun getByFileNameTest() {
        val file = FilesTransferRequest(name = "Test")
        queueSource.add(file)
        Assert.assertNull(queueSource.getByFileName("NotTest"))
        Assert.assertEquals(file, queueSource.getByFileName(file.name!!))
    }

    @Test
    fun getByFilePathTest() {
        val file = FilesTransferRequest(path = "Test")
        queueSource.add(file)
        Assert.assertNull(queueSource.getByFilePath("NotTest"))
        Assert.assertEquals(file, queueSource.getByFilePath(file.path!!))
    }

    @Test
    fun clearTest() {
        val file = FilesTransferRequest(path = "Test")
        val file2 = FilesTransferRequest(path = "Test")
        queueSource.add(listOf(file, file2))
        queueSource.clear()
        Assert.assertTrue(queueSource.isEmpty)
    }

    @Test
    fun getListTest() {
        val file = FilesTransferRequest(path = "Test")
        val file2 = FilesTransferRequest(path = "Test2")
        queueSource.add(listOf(file, file2))

        Assert.assertEquals(listOf(file, file2), queueSource.getList())
    }

    @Test
    fun addTest() {
        val file = FilesTransferRequest(path = "Test")
        queueSource.add(file)
        queueSource.add(file)

        Assert.assertFalse(queueSource.isEmpty)
        Assert.assertTrue(queueSource.getList().size == 1)

        queueSource.clear()

        queueSource.add(listOf(file, FilesTransferRequest()))
        Assert.assertFalse(queueSource.isEmpty)
        Assert.assertTrue(queueSource.getList().size == 2)
    }

    @Test
    fun containsTest() {
        val file = FilesTransferRequest(path = "Test")
        queueSource.add(file)
        Assert.assertTrue(queueSource.contains(file))
        Assert.assertFalse(queueSource.contains(FilesTransferRequest()))
    }

    @Test
    fun saveTest() {
        val file = FilesTransferRequest()
        queueSource.add(file)

        verify(editor).putString(any(), eq(Gson().toJson(listOf(file))))
    }

    @Test
    fun loadTest() {
        val file = FilesTransferRequest(id = 1)
        val file2 = FilesTransferRequest(id = 2)

        whenever(prefs.getString(any(), anyOrNull())).thenReturn(Gson().toJson(listOf(file, file2)))

        Assert.assertEquals(listOf(file, file2), QueueSource(prefs).getList())
    }

}