package gearsoftware.gearhub

import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat


abstract class BaseActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO ->
                    WindowInsetsControllerCompat(window, window.decorView)
                        .isAppearanceLightStatusBars = true
                Configuration.UI_MODE_NIGHT_YES -> WindowInsetsControllerCompat(window, window.decorView)
                    .isAppearanceLightStatusBars = false
            }
        }
    }
}