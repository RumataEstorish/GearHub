package gearsoftware.gearhub.di

import gearsoftware.gearhub.di.providers.OkHttpClientProvider
import okhttp3.OkHttpClient
import toothpick.config.Module
import toothpick.ktp.binding.bind

open class BaseModule : Module() {
    init {
        bind<OkHttpClient>().toProvider(OkHttpClientProvider::class)
    }
}