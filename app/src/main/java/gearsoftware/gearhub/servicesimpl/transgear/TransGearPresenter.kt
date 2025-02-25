package gearsoftware.gearhub.servicesimpl.transgear

import com.google.gson.Gson
import gearsoftware.gearhub.servicesimpl.transgear.data.model.gear.FileTransfer
import gearsoftware.gearhub.servicesimpl.transgear.data.repository.GearMapper
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.InjectConstructor
import java.io.File

@InjectConstructor
class TransGearPresenter(
    private val view: ITransGearView,
    private val interactor: TransGearInteractor
) {

    private val compositeDisposable = CompositeDisposable()


    init {
        interactor.onTransferCompleted
            .subscribeBy(
                onNext = { updateFilesList() },
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        interactor.onProgress
            .subscribeBy(
                onNext = {
                    view.showTransferProgress(
                        progress = it.progress,
                        currentFile = interactor.currentTransferFile?.name,
                        files = interactor.filesList.map { file -> file.path ?: "" }
                    )
                },
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        interactor.onQueueChanged
            .subscribeBy(
                onNext = { files ->
                    view.sendFilesListToWatch(files.map(GearMapper::toGear))
                    view.updateTransferList(
                        filesLeft = files.size,
                        currentTransferPath = interactor.currentTransferFile?.path ?: "",
                        fileNames = files.map { it.name ?: "" }
                    )
                },
                onError = Timber::e
            )
            .addTo(compositeDisposable)

    }

    private fun updateFilesList() {
        val files = interactor.filesList
        if (files.isEmpty()) {
            view.clearTransferList()
        } else {
            view.updateTransferList(files.size, files[0].path!!, files.filter { it.name != null }.map { it.name!! })
        }
    }

    fun onTransferFiles(filesJson: String, isRingtone: Boolean = false) {
        val gson = Gson()
        val files = gson.fromJson(filesJson, Array<File>::class.java)
        interactor.transferFiles(files, isRingtone)
        if (!view.isConnected) {
            view.startConnectToWatch(true)
        }
    }

    fun onCancelCurrentTransfer() {
        interactor.cancelCurrentTransfer()
        view.clearTransferList()
        if (interactor.filesList.isEmpty()) {
            view.stopConnectToWatch()
        }
    }

    fun onCancelAllTransfer() {
        interactor.cancelAllTransfer()
        view.clearTransferList()
        view.stopConnectToWatch()
    }

    fun onCancel(data: String) {
        interactor.onCancel(Gson().fromJson(data, FileTransfer::class.java))
        updateFilesList()
    }

    fun onRepeat() {
        view.sendFilesListToWatch(interactor.filesList.map(GearMapper::toGear))
    }

    fun onReceiveFilesListFromGear(data: String) {
        interactor.onReceiveFilesList(Gson().fromJson(data, arrayOf<FileTransfer>()::class.java))
        updateFilesList()
    }

    fun onConnected(isConnected: Boolean) {
        interactor.onConnectionChanged(isConnected)
        if (isConnected) {
            updateFilesList()
        }
    }
}