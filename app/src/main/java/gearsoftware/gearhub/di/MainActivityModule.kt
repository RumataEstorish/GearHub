package gearsoftware.gearhub.di

import gearsoftware.gearhub.view.main.IMainPresenter
import gearsoftware.gearhub.view.main.IMainView
import gearsoftware.gearhub.view.main.MainPresenter
import toothpick.config.Module
import toothpick.ktp.binding.bind

class MainActivityModule(mainView: IMainView) : Module() {
    init {
        bind<IMainView>().toInstance(mainView)
        bind<IMainPresenter>().toClass<MainPresenter>()
    }
}