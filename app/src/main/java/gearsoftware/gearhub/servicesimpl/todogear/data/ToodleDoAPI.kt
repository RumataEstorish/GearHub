package gearsoftware.gearhub.servicesimpl.todogear.data

import gearsoftware.gearhub.servicesimpl.todogear.data.model.User
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ToodleDoAPI {
    @GET("account/get.php")
    fun getUser(@Query("access_token") accessToken: String): Single<User>
}