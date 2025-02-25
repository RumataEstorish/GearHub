package gearsoftware.gearhub.servicesimpl.squaregear.di.providers

import gearsoftware.gearhub.servicesimpl.squaregear.data.FourSquareAPI
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
class FourSquareAPIProvider(
        private val okHttpClient: OkHttpClient
) : Provider<FourSquareAPI> {
    override fun get(): FourSquareAPI =
        Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl("https://api.foursquare.com/v2/")
                .build()
                .create(FourSquareAPI::class.java)
}