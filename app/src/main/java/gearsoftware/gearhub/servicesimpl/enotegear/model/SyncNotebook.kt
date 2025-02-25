package gearsoftware.gearhub.servicesimpl.enotegear.model

import com.google.gson.annotations.SerializedName

data class SyncNotebook(
        @SerializedName("is_checked")
        var isChecked: Boolean = true,
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String
)