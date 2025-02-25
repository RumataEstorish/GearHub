package gearsoftware.sap.data

import gearsoftware.sap.data.model.WatchText

sealed class GearCommands {
    @Suppress("unused")
    data object OpenApp : GearCommands()
    data class OpenWebPage(val address: String) : GearCommands()
    data class Text(val text: WatchText): GearCommands()
}