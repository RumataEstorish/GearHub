package gearsoftware.gearhub.services.data.model

import com.google.gson.annotations.SerializedName

data class SapPermissionEntity(
    @SerializedName("service_name")
    val serviceName: String,

    @SerializedName("permission")
    val permissions: List<String>,
)
