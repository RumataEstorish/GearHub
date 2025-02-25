package gearsoftware.gearhub.servicesimpl.taskgear

import gearsoftware.sap.data.ISchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.InjectConstructor

@InjectConstructor
class TaskGearPresenter(
    private val view: TaskGearServiceProvider,
    private val taskGearInteractor: TaskGearInteractor,
    private val schedulers: ISchedulers
) {


    private val compositeDisposable = CompositeDisposable()

    fun onConnectedChanged(connected: Boolean) {
        if (!connected) {
            return
        }

        if (taskGearInteractor.isLogin) {
            view.sendAccessTokenToWatch(taskGearInteractor.accessToken)
        } else {
            view.sendAuthNeededToWatch()
        }
    }

    fun onLoginIntent() {
        view.startLoginActivity()
    }

    fun onLogin(accessToken: String) {
        taskGearInteractor.login(accessToken)
        taskGearInteractor.getUser()
            .observeOn(schedulers.mainThread)
            .subscribeBy(
                onNext = view::onUser,
                onError = Timber::e
            )
            .addTo(compositeDisposable)
        view.sendAccessTokenToWatch(accessToken)
        view.sendLoggedIn()
    }

    fun onAuthNeededReceived() {
        if (taskGearInteractor.isLogin) {
            view.sendAccessTokenToWatch(taskGearInteractor.accessToken, true)
        } else {
            view.sendAuthNeededToWatch()
        }
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

    fun onLogoutIntent() {
        if (taskGearInteractor.isLogin) {
            taskGearInteractor.logout()
            view.sendAuthNeededToWatch()
            view.sendLoggedOut()
        }
    }

    fun onLoginFailed() {

    }
}