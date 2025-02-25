package gearsoftware.sap.data.model

/**
 * Data from watch
 */
data class WatchText(
        /**
         * Channel id
         */
        val channelId: Int,

        /**
         * Data in custom format
         */
        val data: String = ""
)