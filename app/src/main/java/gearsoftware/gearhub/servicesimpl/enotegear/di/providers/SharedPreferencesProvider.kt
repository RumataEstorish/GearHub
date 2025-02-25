package gearsoftware.gearhub.servicesimpl.enotegear.di.providers

import android.content.Context
import android.content.SharedPreferences
import gearsoftware.gearhub.servicesimpl.enotegear.ENoteGearServiceProvider.Companion.ENOTEGEAR_PREF
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
class SharedPreferencesProvider(
        private val context: Context
) : Provider<SharedPreferences> {
    override fun get(): SharedPreferences =
            context.getSharedPreferences(ENOTEGEAR_PREF, Context.MODE_PRIVATE)
}