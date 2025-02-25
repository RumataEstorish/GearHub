package gearsoftware.gearhub.servicesimpl.enotegear

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.google.gson.Gson
import gearsoftware.gearhub.servicesimpl.enotegear.model.SyncNotebook
import toothpick.InjectConstructor

@InjectConstructor
class Settings(
        private val sharedPreferences: SharedPreferences
) {

    companion object {
        const val SYNC_CONTENT = "SYNC_CONTENT"
        const val SYNC_ALL_NOTEBOOKS = "SYNC_ALL_NOTEBOOKS"
        const val SYNC_NOTEBOOKS_LIST = "SYNC_NOTEBOOKS_LIST"
    }

    var syncContent: Boolean
        get() = sharedPreferences.getBoolean(SYNC_CONTENT, false)
        @SuppressLint("ApplySharedPref")
        set(value) {
            sharedPreferences.edit().putBoolean(SYNC_CONTENT, value).commit()
        }

    var allNotebooks: Boolean
        get() = sharedPreferences.getBoolean(SYNC_ALL_NOTEBOOKS, true)
        @SuppressLint("ApplySharedPref")
        set(value) {
            sharedPreferences.edit().putBoolean(SYNC_ALL_NOTEBOOKS, value).commit()
        }

    private var _syncNotebooks: List<SyncNotebook> = emptyList()
    var syncNotebooks: List<SyncNotebook>
        get() = _syncNotebooks
        @SuppressLint("ApplySharedPref")
        set(value) {
            _syncNotebooks = value
            sharedPreferences.edit().putString(SYNC_NOTEBOOKS_LIST, Gson().toJson(value)).commit()
        }

    init {
        _syncNotebooks = sharedPreferences.getString(SYNC_NOTEBOOKS_LIST, "").let {
            if (it.isNullOrBlank()) {
                emptyList()
            } else {
                Gson().fromJson(it, _syncNotebooks::class.java)
            }
        }
    }
}