package gearsoftware.gearhub.servicesimpl.transgear.data.repository

import com.nhaarman.mockitokotlin2.*
import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import gearsoftware.gearhub.servicesimpl.transgear.data.sources.IQueueSource
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class QueueRepositoryTest {
    private val queueSource: IQueueSource = mock()
    private val gearMapper: GearMapper = mock()
    private lateinit var queueRepository: IQueueRepository

    @Before
    fun create() {
         queueRepository = QueueRepository(queueSource, gearMapper)
    }


    @Test
    fun getAllTest() {
        queueRepository.getAll()
        verify(queueSource).getList()
    }

    @Test
    fun hasFilesTest() {
        whenever(queueSource.isEmpty).thenReturn(false)
        Assert.assertEquals(true, queueRepository.hasFiles())

        whenever(queueSource.isEmpty).thenReturn(true)
        Assert.assertEquals(false, queueRepository.hasFiles())

        verify(queueSource, times(2)).isEmpty
    }

    @Test
    fun getByIdTest() {
        queueRepository.getById(10)
        verify(queueSource).getByTransferId(eq(10))
    }

    @Test
    fun addTest() {
        val files = listOf(FilesTransferRequest())
        queueRepository.add(files)
        verify(queueSource).add(eq(files))
    }
}