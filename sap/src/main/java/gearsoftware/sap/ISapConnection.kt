package gearsoftware.sap

import gearsoftware.sap.data.model.WatchText
import io.reactivex.rxjava3.core.Observable

internal interface ISapConnection {

    val onReceive: Observable<WatchText>
    val onServiceConnectionLost: Observable<Int>

    val isGearConnected: Boolean

    fun send(channelId: Int, data: String)
}