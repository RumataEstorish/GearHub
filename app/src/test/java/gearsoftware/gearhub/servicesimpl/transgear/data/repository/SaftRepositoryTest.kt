package gearsoftware.gearhub.servicesimpl.transgear.data.repository

import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest.Companion.NO_TRANSFER
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftCompleted
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftProgress
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftReceive
import gearsoftware.gearhub.servicesimpl.transgear.data.sources.ISAFTSource
import io.reactivex.rxjava3.subjects.PublishSubject

class SaftRepositoryTest {

    private val saftSource: ISAFTSource = mock()
    private lateinit var saftRepository: SaftRepository

    @Before
    fun create() {
        whenever(saftSource.onCancelAll).thenReturn(PublishSubject.create())
        whenever(saftSource.onProgress).thenReturn(PublishSubject.create())
        whenever(saftSource.onReceive).thenReturn(PublishSubject.create())
        whenever(saftSource.onTransferCompleted).thenReturn(PublishSubject.create())

        saftRepository = SaftRepository(saftSource)
    }


    @Test
    fun onFileReceiveTest() {
        val test = saftRepository.onFileReceive.test()
        val source = SaftReceive(10, "test", "testpath")
        saftSource.onReceive.onNext(source)
        test.awaitCount(1).assertValue(source)

        val source2 = SaftReceive(11, "test2", "testpath2")
        saftSource.onReceive.onNext(source2)
        test.awaitCount(1).assertValues(source, source2)
    }

    @Test
    fun onProgressTest() {
        val source = SaftProgress(10, 90)
        val test = saftRepository.onProgress.test()

        saftSource.onProgress.onNext(source)
        test.awaitCount(1).assertValue(source)
    }

    @Test
    fun onFilesCanceledTest() {
        val test = saftRepository.onFilesCanceled.test()
        saftSource.onCancelAll.onNext(Unit)

        test.awaitCount(1).assertValue(Unit)
    }

    @Test
    fun onFileCompletedTest() {
        val source = SaftCompleted(10, "noPath", code = 0)
        val test = saftRepository.onFileCompleted.test()
        saftSource.onTransferCompleted.onNext(source)
        test.awaitCount(1).assertValue(source)
    }

    @Test
    fun sendListTest() {
        val f1 = FilesTransferRequest()
        val f2 = FilesTransferRequest(path = "TestPath")

        whenever(saftSource.send(any())).thenReturn(100)

        Assert.assertEquals(listOf(NO_TRANSFER, 100), saftRepository.send(listOf(f1, f2)))
        verify(saftSource).send(eq(f2.path!!))
    }

    @Test
    fun sendTest() {
        val f1 = FilesTransferRequest()
        val f2 = FilesTransferRequest(path = "TestPath2")

        whenever(saftSource.send(any())).thenReturn(100)

        Assert.assertEquals(NO_TRANSFER, saftRepository.send(f1))
        verify(saftSource, never()).send(eq(f2.path!!))

        Assert.assertEquals(100, saftRepository.send(f2))
        verify(saftSource).send(eq(f2.path!!))
    }

    @Test
    fun cancelTest() {

        val f = FilesTransferRequest(10)

        saftRepository.cancel(f.id)

        verify(saftSource).cancel(eq(f.id))
    }

    @Test
    fun cancelAllTest() {
        saftRepository.cancelAll()

        verify(saftSource).cancelAll()
    }
}