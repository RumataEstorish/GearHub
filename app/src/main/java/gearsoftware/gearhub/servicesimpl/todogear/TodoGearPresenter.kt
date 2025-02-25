package gearsoftware.gearhub.servicesimpl.todogear

import gearsoftware.gearhub.servicesimpl.todogear.data.model.LoginAccessToken
import gearsoftware.sap.data.ISchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.InjectConstructor

@InjectConstructor
class TodoGearPresenter(
    private val view: TodoGearServiceProvider,
    private val todoGearInteractor: TodoGearInteractor,
    private val schedulers: ISchedulers
) {

    private val compositeDisposable = CompositeDisposable()

    fun onDestroy() {
        compositeDisposable.clear()
    }

    fun onLogin(loginAccessToken: LoginAccessToken) {
        todoGearInteractor.login(loginAccessToken)

        todoGearInteractor.getUser()
            .observeOn(schedulers.mainThread)
            .subscribeBy(
                onSuccess = view::onUser,
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        view.sendToken(loginAccessToken)
        view.sendLoggedIn()
    }

    fun onConnectedChanged(connected: Boolean) {
        if (!connected) {
            return
        }

        if (!todoGearInteractor.isLoggedIn()) {
            view.sendAuthNeeded()
        }
    }

    fun onAuthNeededReceive() {
        if (todoGearInteractor.isLoggedIn()) {
            view.sendToken(todoGearInteractor.token!!, true)
        } else {
            view.sendAuthNeeded()
        }
    }

    fun onReauthNeeded() {
        onLogout()
    }

    fun onLogout() {
        if (todoGearInteractor.isLoggedIn()) {
            todoGearInteractor.logout()
            view.sendAuthNeeded()
            view.sendLoggedOut()
        }
    }

    fun onLoginIntent() {
        view.startLoginActivity()
    }

    fun onLoginFailed() {

    }
}