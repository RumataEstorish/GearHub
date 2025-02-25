package gearsoftware.gearhub.servicesimpl.taskgear.data.model

import com.google.gson.annotations.SerializedName

/**
 * User response from server
 */
data class UserResponse(
        @SerializedName("user")
        val user: User? = null,
        @SerializedName("sync_token")
        val syncToken: String? = null
)
