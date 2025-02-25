package gearsoftware.gearhub.serviceprovider

data class ChannelInfo(
        val id: String,
        val name: String,
        val description: String = "",
        val group: ChannelGroup? = null,
        val importance: Int = 1,
        val showBadge: Boolean = false
)