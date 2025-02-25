package gearsoftware.gearhub.servicesimpl.enotegear

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import gearsoftware.gearhub.servicesimpl.enotegear.ENoteGearServiceProvider.Companion.ENOTEGEAR_NAME
import gearsoftware.settings.SettingsBaseActivity


class SettingsActivity : SettingsBaseActivity<SettingsFragment>() {

    companion object {
        const val ENOTEGEAR_SETTINGS_ACTIVITY = "gearsoftware.enotegear.SETTINGS_ACTIVITY"
    }

    override fun getFragment(): SettingsFragment =
        SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        supportActionBar?.title = ENOTEGEAR_NAME
        title = ENOTEGEAR_NAME
    }

    override fun onBackPressed() {
        sendBroadcast(Intent(ENOTEGEAR_SETTINGS_ACTIVITY).apply { setPackage(this.`package`) })
        super.onBackPressed()
    }
}