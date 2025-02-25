package gearsoftware.gearhub.servicesimpl.taskgear.di.providers

import gearsoftware.gearhub.servicesimpl.taskgear.data.TodoistAPI
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
class TodoistAPIProvider : Provider<TodoistAPI> {
    override fun get(): TodoistAPI =
            Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .baseUrl("https://todoist.com/API/v8/")
                    .build()
                    .create(TodoistAPI::class.java)
}