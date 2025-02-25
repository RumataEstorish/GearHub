package gearsoftware.gearhub.servicesimpl.enotegear

import gearsoftware.sap.data.ISchedulers
import gearsoftware.sap.data.model.WatchText
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.InjectConstructor

@InjectConstructor
class ENoteGearPresenter(
    private val view: ENoteGearServiceProvider,
    private val eNoteGearProcessor: ENoteGearProcessor,
    private val schedulers: ISchedulers
) {

    private val compositeDisposable = CompositeDisposable()

    init {
        eNoteGearProcessor.syncStatus
            .subscribeBy(
                onNext = view::updateStatus,
                onError = Timber::e
            )
            .addTo(compositeDisposable)
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

    fun onReceive(data: WatchText) {
        if (!eNoteGearProcessor.isLoggedIn) {
            view.sendAuthNeeded()
        } else {
            eNoteGearProcessor
                .process(data.data)
                .doOnSubscribe { view.startLongTermOperation() }
                .subscribeBy(
                    onNext = view::sendData,
                    onError = Timber::e,
                    onComplete = view::stopLongTermOperation
                )
                .addTo(compositeDisposable)
        }
    }

    fun onLogout() {
        if (eNoteGearProcessor.isLoggedIn) {
            eNoteGearProcessor.logout()
            view.sendAuthNeeded()
            view.sendLoggedOut()
        }
    }

    fun onLoginResult() {
        if (eNoteGearProcessor.isLoggedIn) {
            view.sendLoggedIn()

            eNoteGearProcessor.getUser()
                .map { it.name }
                .observeOn(schedulers.mainThread)
                .subscribeBy(
                    onSuccess = view::sendUserName,
                    onError = Timber::e
                )
                .addTo(compositeDisposable)
        }
    }

    fun onLoginIntent() {
        view.startLoginActivity()
    }
}