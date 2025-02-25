package gearsoftware.gearhub.socialgear

import gearsoftware.gearhub.socialgear.data.model.VKLoginResult
import gearsoftware.sap.data.ISchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.InjectConstructor

@InjectConstructor
class SocialGearPresenter(
    private val view: SocialGearProviderService,
    private val socialGearInteractor: SocialGearInteractor,
    private val schedulers: ISchedulers
) {

    private val compositeDisposable = CompositeDisposable()


    fun onLogin(accessToken: VKLoginResult) {
        socialGearInteractor.login(accessToken)

        view.sendLoggedIn()
        view.sendAccessToken(accessToken.accessToken)

        socialGearInteractor.getUser()
            .observeOn(schedulers.mainThread)
            .subscribeBy(
                onSuccess = view::setUser,
                onError = Timber::e
            )
            .addTo(compositeDisposable)
    }

    fun onTokenExpired() {
        view.onLogoutIntent()
    }

    fun onLoginFailed() {
        view.showLoginError()
    }

    fun onAuthNeeded() {
        if (socialGearInteractor.isLoggedIn) {
            view.sendAccessToken(socialGearInteractor.accessToken)
        } else {
            view.sendAuthNeeded()
        }
    }

    fun onConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            return
        }
        if (socialGearInteractor.isLoggedIn) {
            view.sendAccessToken(socialGearInteractor.accessToken)
        } else {
            view.sendAuthNeeded()
        }
    }

    fun onLogoutIntent() {
        if (socialGearInteractor.isLoggedIn) {
            socialGearInteractor.logout()
            view.sendAuthNeeded()
            view.sendLoggedOut()
        }
    }

    fun onLoginIntent() {
        view.startLoginActivity()
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }
}