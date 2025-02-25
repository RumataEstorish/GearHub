package gearsoftware.gearhub.services.data.model

import com.google.gson.annotations.SerializedName

data class ServiceUserLogin(
        @SerializedName("serviceName")
        val serviceName: String = "",
        @SerializedName("userName")
        val userName: String = ""
)