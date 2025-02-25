package gearsoftware.gearhub.servicesimpl.transgear

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gearsoftware.gearhub.di.Scopes
import toothpick.ktp.KTP

open class TransGearActivity : AppCompatActivity() {

    companion object {
        const val SEND_FILES_ACTION = "gearsoftware.gearhub.servicesimpl.transgear.SEND_FILES_ACTION"
        const val SEND_FILES_EXTRA = "SEND_FILES_EXTRA"
    }


    // private val serviceManager: ServicesManager by inject<ServicesManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KTP.openScope(Scopes.APP)
                .inject(this)

        // TODO send cmd to service
        /*serviceManager.startService((Intent(this, TransGearProviderService::class.java).apply {
            action = SEND_FILES_ACTION
            putExtra(SEND_FILES_EXTRA, Gson().toJson(Utils.parseFileIntent(this@TransGearActivity, intent)))
        }))*/

        finish()
    }


}
