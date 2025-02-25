package gearsoftware.gearhub.servicesimpl.transgear.di.providers

import android.content.Context
import android.content.SharedPreferences
import gearsoftware.gearhub.servicesimpl.transgear.TransGearProviderService.Companion.TRANSGEAR_PREF
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
class SharedPreferencesProvider (
        private val context: Context
) : Provider<SharedPreferences> {
    override fun get(): SharedPreferences =
            context.getSharedPreferences(TRANSGEAR_PREF, Context.MODE_PRIVATE)
}