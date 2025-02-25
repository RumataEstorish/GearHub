package gearsoftware.gearhub.servicesimpl.squaregear.model

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

data class User(

        @SerializedName("id")
        val id: Long = 0,

        @SerializedName("firstName")
        val firstName: String = "",

        @SerializedName("lastName")
        val lastName: String = "",

        @SerializedName("gender")
        val gender: String = "",

        @SerializedName("homeCity")
        val homeCity: String = "",

        @SerializedName("bio")
        val bio: String = "",

        @SerializedName("contact")
        val contact: UserContact? = null,

        @SerializedName("bitmapPhoto")
        val bitmapPhoto: Bitmap? = null,

        @SerializedName("relationship")
        val relationship: String? = null,

        @SerializedName("photo")
        val photo: UserPhoto? = null
) {
    val name: String
        get() {
            if (lastName.isNotBlank() && firstName.isNotEmpty()) {
                return String.format("%s %s", firstName, lastName)
            }

            return if (lastName.isNotEmpty()) {
                lastName
            } else {
                firstName
            }
        }
}
