package gearsoftware.gearhub.servicesimpl.squaregear.model

import com.google.gson.annotations.SerializedName

data class Response<T>(
        @SerializedName("response")
        val response: T
)