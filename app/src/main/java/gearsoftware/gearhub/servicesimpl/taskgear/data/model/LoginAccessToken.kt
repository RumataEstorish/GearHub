package gearsoftware.gearhub.servicesimpl.taskgear.data.model

import com.google.gson.annotations.SerializedName

/**
 * Login access token data
 */
data class LoginAccessToken(
        @SerializedName("error")
        val error: String = "",

        @SerializedName("access_token")
        val accessToken: String = "",

        @SerializedName("token_type")
        val tokenType: String = ""
)
