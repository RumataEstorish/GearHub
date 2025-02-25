package gearsoftware.sap

import gearsoftware.sap.data.GearCommands
import gearsoftware.sap.data.GearStates
import io.reactivex.rxjava3.core.Observable

interface ISap {

    /**
     * Model of connected watch
     */
    val connectedModel: String

    /**
     * Check if connection succeed
     */
    val isConnected: Boolean

    /**
     * Request from watch
     */
    val onGearCommand: Observable<GearCommands>

    /**
     * Gear communication state
     */
    val onGearState: Observable<GearStates>

    /**
     * Connect to watch
     */
    fun startConnectToWatch(endlessAttempts: Boolean = false)

    /**
     * Stop connecting to watch
     */
    fun stopConnectToWatch()

    /**
     * Stop send data to watch
     */
    fun stopSendData()

    /**
     * Stop network request from watch
     */
    fun stopNetRequest()

    /**
     * Stop location request from watch
     */
    fun stopLocationRequest()
}
