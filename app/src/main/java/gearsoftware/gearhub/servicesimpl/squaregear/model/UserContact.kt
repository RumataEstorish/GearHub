package gearsoftware.gearhub.servicesimpl.squaregear.model

import com.google.gson.annotations.SerializedName

data class UserContact(
        @SerializedName("email")
        val email: String = "",

        @SerializedName("facebook")
        val facebook: String = "",

        @SerializedName("twitter")
        val twitter: String = "",

        @SerializedName("phone")
        val phone: String = ""
)
