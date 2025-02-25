package gearsoftware.sap.di.provider

import gearsoftware.sap.data.gearhttp.NetRequestSource
import okhttp3.OkHttpClient
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
internal class NetRequestSourceProvider : Provider<NetRequestSource> {
    override fun get(): NetRequestSource =
            NetRequestSource(OkHttpClient())
}