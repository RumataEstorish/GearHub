package gearsoftware.gearhub.di.providers

import okhttp3.OkHttpClient
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
class OkHttpClientProvider : Provider<OkHttpClient> {
    override fun get(): OkHttpClient =
            OkHttpClient.Builder()
                    .build()
}