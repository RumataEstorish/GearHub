package gearsoftware.sap.data

sealed class GearStates {
    data class Connected(val isConnected: Boolean) : GearStates()
    data object PeerAgentConnected : GearStates()
    data object PeerAgentDisconnected : GearStates()
    data object ConnectingStarted: GearStates()
    data object ConnectingStopped: GearStates()
    data object SendingStarted: GearStates()
    data object SendingStopped: GearStates()
    data object LocationRequestStarted: GearStates()
    data object LocationRequestStopped: GearStates()
    data object NetworkRequestStarted: GearStates()
    data object NetworkRequestStopped: GearStates()
}