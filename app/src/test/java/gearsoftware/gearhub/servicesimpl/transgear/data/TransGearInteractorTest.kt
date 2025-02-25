package gearsoftware.gearhub.servicesimpl.transgear.data

import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import gearsoftware.sap.Sap
import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import gearsoftware.gearhub.servicesimpl.transgear.TransGearInteractor
import gearsoftware.gearhub.servicesimpl.transgear.data.repository.IQueueRepository
import gearsoftware.gearhub.servicesimpl.transgear.data.repository.ISaftRepository
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Ignore
import java.io.File

class TransGearInteractorTest {

    private val saftRepository: ISaftRepository = mock()
    private val queueRepository: IQueueRepository = mock()
    private val sap: Sap = mock()

    private lateinit var interactor: TransGearInteractor

    @Before
    fun create() {
        whenever(saftRepository.onProgress).doReturn(PublishSubject.create())
        whenever(saftRepository.onFileCompleted).doReturn(PublishSubject.create())
        interactor = TransGearInteractor(saftRepository, queueRepository, sap)
    }

    @Ignore("Fix")
    @Test
    fun transferFilesNotConnectedTest() {
        val file1 = File("test")
        val file2 = File("test2")

        //whenever(queueRepository.getByFile(any())).thenReturn(null)

        interactor.transferFiles(arrayOf(file1, file2), false)

        verify(queueRepository).add(eq(listOf(FilesTransferRequest(file1, false), FilesTransferRequest(file2, false))))
    }

    @Ignore("fix")
    @Test
    fun transferFilesConnectedTest() {
        val file1 = File("test")
        val file2 = File("test2")


        whenever(saftRepository.send(eq(FilesTransferRequest(file1)))).doReturn(100)
        whenever(saftRepository.send(eq(FilesTransferRequest(file2)))).doReturn(200)

        interactor.transferFiles(arrayOf(file1, file2), true)
    }
}