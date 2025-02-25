package gearsoftware.gearhub.servicesimpl.todogear.di

import android.content.SharedPreferences
import gearsoftware.gearhub.di.BaseModule
import gearsoftware.gearhub.serviceprovider.AbstractSapService
import gearsoftware.gearhub.servicesimpl.todogear.TodoGearPresenter
import gearsoftware.gearhub.servicesimpl.todogear.TodoGearServiceProvider
import gearsoftware.gearhub.servicesimpl.todogear.data.ToodleDoAPI
import gearsoftware.gearhub.servicesimpl.todogear.di.providers.*
import toothpick.ktp.binding.bind

class TodoGearModule(
        todoGearServiceProvider: TodoGearServiceProvider
) : BaseModule() {
    init {
        bind<AbstractSapService>().toInstance(todoGearServiceProvider)
        bind<TodoGearServiceProvider>().toInstance(todoGearServiceProvider)
        bind<TodoGearPresenter>()
        bind<ToodleDoAPI>().toProvider(ToodleDoAPIProvider::class)
        bind<SharedPreferences>().toProvider(SharedPreferencesProvider::class).providesSingleton()
    }
}