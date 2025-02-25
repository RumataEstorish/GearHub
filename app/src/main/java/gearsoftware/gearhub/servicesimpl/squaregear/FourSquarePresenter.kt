package gearsoftware.gearhub.servicesimpl.squaregear

import gearsoftware.sap.data.ISchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.InjectConstructor

@InjectConstructor
class FourSquarePresenter(
    private val view: FourSquareAccessoryProvider,
    private val interactor: FourSquareInteractor,
    private val schedulers: ISchedulers
) {

    private val compositeDisposable = CompositeDisposable()


    fun onConnectedChanged(connected: Boolean) {
        if (!connected) {
            return
        }

        if (interactor.isLoggedIn) {
            view.sendAccessTokenToWatch(interactor.accessToken)
        } else {
            view.sendAuthNeededToWatch()
        }
    }

    fun onLoginIntent() {
        view.startLoginActivity()
    }

    fun onLogin(accessToken: String) {
        interactor.login(accessToken)

        interactor.getUser()
            .observeOn(schedulers.mainThread)
            .subscribeBy(
                onSuccess = { view.sendUserName(it.name) },
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        view.sendAccessTokenToWatch(accessToken)
        view.sendLoggedIn()
    }

    fun onAuthNeededReceived() {
        if (interactor.isLoggedIn) {
            view.sendAccessTokenToWatch(interactor.accessToken, true)
        } else {
            view.sendAuthNeededToWatch()
        }
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

    fun onLogoutIntent() {
        if (interactor.isLoggedIn) {
            interactor.logout()
            view.sendAuthNeededToWatch()
            view.sendLoggedOut()
        }
    }
}