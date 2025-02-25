package gearsoftware.gearhub.servicesimpl.squaregear.data

import gearsoftware.gearhub.servicesimpl.squaregear.model.Response
import gearsoftware.gearhub.servicesimpl.squaregear.model.UserResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface FourSquareAPI {
    @GET("users/self")
    fun getUser(
        @Query("v") apiDateVersion: String,
        @Query("oauth_token") accessToken: String,
        @Query("m") mode: String
    ): Single<Response<UserResponse>>
}