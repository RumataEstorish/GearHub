package gearsoftware.gearhub

import androidx.multidex.MultiDexApplication
import gearsoftware.gearhub.di.AppModule
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.di.ServiceCommunicationModule
import gearsoftware.sap.di.SapCommonModule
import timber.log.Timber
import toothpick.configuration.Configuration
import toothpick.ktp.KTP


/**
 * Application class
 */
@Suppress("unused")
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            KTP.setConfiguration(Configuration.forDevelopment())
            Timber.plant(Timber.DebugTree())
        }

        KTP.openScopes(Scopes.APP)
            .installModules(AppModule(this), SapCommonModule(), ServiceCommunicationModule())
    }
}
