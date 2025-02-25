package gearsoftware.gearhub.servicesimpl.todogear.di.providers

import android.content.Context
import android.content.SharedPreferences
import gearsoftware.gearhub.servicesimpl.todogear.TodoGearServiceProvider.Companion.TODOGEAR_PREFS
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
class SharedPreferencesProvider(
        private val context: Context
) : Provider<SharedPreferences> {
    override fun get(): SharedPreferences =
            context.getSharedPreferences(TODOGEAR_PREFS, Context.MODE_PRIVATE)
}