package gearsoftware.gearhub.services

data class SapPermission(
    val permissions: List<String>,
    val title: String,
    val description: String,
    val required: Boolean
)