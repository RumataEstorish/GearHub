package gearsoftware.gearhub.socialgear.data.model

import com.google.gson.annotations.SerializedName
import com.vk.api.sdk.auth.VKAccessToken

data class VKLoginResult(
        @SerializedName("access_token")
        val accessToken: String,
        @SerializedName("created")
        val created: Long,
        @SerializedName("email")
        val email: String?,
        @SerializedName("is_valid")
        val isValid: Boolean,
        @SerializedName("phone")
        val phone: String?,
        @SerializedName("phone_access_key")
        val phoneAccessKey: String?,
        @SerializedName("secret")
        val secret: String?,
        @SerializedName("user_id")
        val userId: Long
) {
    constructor(vkAccessToken: VKAccessToken) : this(
            vkAccessToken.accessToken,
            vkAccessToken.created,
            vkAccessToken.email,
            vkAccessToken.isValid,
            vkAccessToken.phone,
            vkAccessToken.phoneAccessKey,
            vkAccessToken.secret,
            vkAccessToken.userId.value
    )
}