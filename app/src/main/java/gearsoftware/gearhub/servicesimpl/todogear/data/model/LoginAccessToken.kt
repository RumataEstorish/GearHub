package gearsoftware.gearhub.servicesimpl.todogear.data.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * Todogear onLoginIntent access token
 */
data class LoginAccessToken(
        @SerializedName("refresh_token")
        val refreshToken: String = "",

        @SerializedName("access_token")
        val accessToken: String = "",

        @SerializedName("expires_in")
        val expiresIn: Int = 0,

        @SerializedName("scope")
        val scope: String = "",

        @SerializedName("token_type")
        val tokenType: String = ""
) {
    val isEmpty: Boolean
        get() = refreshToken.isEmpty() || accessToken.isEmpty()

    fun toJson(): String =
            Gson().toJson(this)
}
