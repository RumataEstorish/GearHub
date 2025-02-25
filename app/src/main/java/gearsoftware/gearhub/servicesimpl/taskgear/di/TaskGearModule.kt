package gearsoftware.gearhub.servicesimpl.taskgear.di

import android.content.SharedPreferences
import gearsoftware.gearhub.di.BaseModule
import gearsoftware.gearhub.serviceprovider.AbstractSapService
import gearsoftware.gearhub.servicesimpl.taskgear.TaskGearPresenter
import gearsoftware.gearhub.servicesimpl.taskgear.TaskGearServiceProvider
import gearsoftware.gearhub.servicesimpl.taskgear.data.TodoistAPI
import gearsoftware.gearhub.servicesimpl.taskgear.di.providers.*
import toothpick.ktp.binding.bind

class TaskGearModule(
        taskGearServiceProvider: TaskGearServiceProvider
) : BaseModule() {
    init {
        bind<AbstractSapService>().toInstance(taskGearServiceProvider)
        bind<TaskGearServiceProvider>().toInstance(taskGearServiceProvider)
        bind<TaskGearPresenter>()
        bind<TodoistAPI>().toProvider(TodoistAPIProvider::class)
        bind<SharedPreferences>().toProvider(SharedPreferencesProvider::class).providesSingleton()
    }
}