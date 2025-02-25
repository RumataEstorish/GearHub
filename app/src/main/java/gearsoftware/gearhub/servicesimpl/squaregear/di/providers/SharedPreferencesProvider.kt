package gearsoftware.gearhub.servicesimpl.squaregear.di.providers

import android.content.Context
import android.content.SharedPreferences
import gearsoftware.gearhub.servicesimpl.squaregear.FourSquareAccessoryProvider.Companion.SQUAREGEAR_PREF
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
class SharedPreferencesProvider (
        private val context: Context
) : Provider<SharedPreferences> {
    override fun get(): SharedPreferences =
            context.getSharedPreferences(SQUAREGEAR_PREF, Context.MODE_PRIVATE)
}