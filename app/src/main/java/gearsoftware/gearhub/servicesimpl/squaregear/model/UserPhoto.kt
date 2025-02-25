package gearsoftware.gearhub.servicesimpl.squaregear.model

import com.google.gson.annotations.SerializedName

data class UserPhoto(

        @SerializedName("prefix")
        val prefix: String = "",

        @SerializedName("suffix")
        val suffix: String = ""
)
