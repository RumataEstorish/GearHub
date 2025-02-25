package gearsoftware.gearhub.servicesimpl.transgear.data.sources

import com.samsung.android.sdk.accessory.SAAgent
import com.samsung.android.sdk.accessory.SAPeerAgent
import com.samsung.android.sdk.accessoryfiletransfer.SAFileTransfer
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftCompleted
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftProgress
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftReceive
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import java.io.Closeable
import java.io.File

//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
class SAFTSource(saPeerAgentChangeSubject: Observable<SAPeerAgent>, saAgent: SAAgent, savePath: File) : ISAFTSource, Closeable {

    @Volatile
    private lateinit var peerAgent: SAPeerAgent
    private val compositeDisposable = CompositeDisposable()

    init {
        saPeerAgentChangeSubject
            .subscribeBy(
                onNext = { peerAgent = it },
                onError = Timber::e
            )
            .addTo(compositeDisposable)
    }

    override val onProgress: PublishSubject<SaftProgress> = PublishSubject.create()
    override val onReceive: PublishSubject<SaftReceive> = PublishSubject.create()
    override val onCancelAll: PublishSubject<Unit> = PublishSubject.create()
    override val onTransferCompleted: PublishSubject<SaftCompleted> = PublishSubject.create()

    private val eventListener = object : SAFileTransfer.EventListener {
        override fun onProgressChanged(id: Int, progress: Int) {
            onProgress.onNext(SaftProgress(id, progress))
        }

        override fun onTransferRequested(id: Int, fileName: String) {
            val path = File(savePath, File(fileName).name).absolutePath
            onReceive.onNext(SaftReceive(id, fileName, path))
            saTransfer.receive(id, path)
        }

        override fun onCancelAllCompleted(p0: Int) {
            onCancelAll.onNext(Unit)
        }

        override fun onTransferCompleted(id: Int, filePath: String?, errorCode: Int) {
            onTransferCompleted.onNext(SaftCompleted(id, filePath, errorCode))
        }
    }

    private val saTransfer: SAFileTransfer = SAFileTransfer(saAgent, eventListener)

    override fun cancel(id: Int) {
        saTransfer.cancel(id)
    }

    override fun cancelAll() {
        saTransfer.cancelAll()
    }

    @Throws(UninitializedPropertyAccessException::class)
    override fun send(filePath: String): Int {
        if (!::peerAgent.isInitialized) {
            throw UninitializedPropertyAccessException("peerAgent is not initialized")
        }
        return saTransfer.send(peerAgent, filePath)
    }


    override fun close() {
        saTransfer.close()
        compositeDisposable.clear()
    }
}