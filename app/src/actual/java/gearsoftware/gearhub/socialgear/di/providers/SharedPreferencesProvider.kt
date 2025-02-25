package gearsoftware.gearhub.socialgear.di.providers

import android.content.Context
import android.content.SharedPreferences
import gearsoftware.gearhub.socialgear.SocialGearProviderService.Companion.SOCIALGEAR_PREFS
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
class SharedPreferencesProvider(
        private val context: Context
) : Provider<SharedPreferences> {
    override fun get(): SharedPreferences =
            context.getSharedPreferences(SOCIALGEAR_PREFS, Context.MODE_PRIVATE)
}