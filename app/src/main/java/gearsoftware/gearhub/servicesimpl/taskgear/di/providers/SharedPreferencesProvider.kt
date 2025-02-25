package gearsoftware.gearhub.servicesimpl.taskgear.di.providers

import android.content.Context
import android.content.SharedPreferences
import gearsoftware.gearhub.servicesimpl.taskgear.TaskGearServiceProvider.Companion.TASKGEAR_PREFS
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
class SharedPreferencesProvider(
        private val context: Context
) : Provider<SharedPreferences> {
    override fun get(): SharedPreferences =
            context.getSharedPreferences(TASKGEAR_PREFS, Context.MODE_PRIVATE)
}