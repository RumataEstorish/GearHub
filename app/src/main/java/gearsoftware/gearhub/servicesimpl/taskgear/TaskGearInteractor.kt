package gearsoftware.gearhub.servicesimpl.taskgear

import gearsoftware.gearhub.servicesimpl.taskgear.data.TodoistRepository
import gearsoftware.gearhub.servicesimpl.taskgear.data.model.User
import io.reactivex.rxjava3.core.Observable
import toothpick.InjectConstructor

@InjectConstructor
class TaskGearInteractor(
        private val todoistRepository: TodoistRepository
) {

    val accessToken: String
        get() = todoistRepository.accessToken

    val isLogin: Boolean
        get() = todoistRepository.accessToken.isNotBlank()

    fun login(accessToken: String) {
        todoistRepository.accessToken = accessToken
    }

    fun logout() =
            todoistRepository.logout()

    fun getUser(): Observable<User> =
            todoistRepository.getUser()
}