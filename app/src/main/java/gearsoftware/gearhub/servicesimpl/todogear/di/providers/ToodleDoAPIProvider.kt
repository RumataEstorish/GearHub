package gearsoftware.gearhub.servicesimpl.todogear.di.providers

import gearsoftware.gearhub.servicesimpl.todogear.data.ToodleDoAPI
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
class ToodleDoAPIProvider : Provider<ToodleDoAPI> {
    override fun get(): ToodleDoAPI =
            Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .baseUrl("https://api.toodledo.com/3/")
                    .build()
                    .create(ToodleDoAPI::class.java)
}