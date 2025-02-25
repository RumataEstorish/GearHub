package gearsoftware.gearhub.servicesimpl.taskgear.data

import gearsoftware.gearhub.servicesimpl.taskgear.data.model.UserResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface TodoistAPI {
    @GET("sync")
    fun getUser(@Query("token") accessToken: String,
                @Query("sync_token") syncToken: String,
                @Query("resource_types") resourseTypes: String): Observable<UserResponse>
}