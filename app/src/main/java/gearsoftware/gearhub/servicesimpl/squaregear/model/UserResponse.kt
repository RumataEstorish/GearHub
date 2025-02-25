package gearsoftware.gearhub.servicesimpl.squaregear.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
        @SerializedName("user")
        val user: User
)