package gearsoftware.gearhub.servicesimpl.enotegear

import android.os.Bundle
import gearsoftware.gearhub.R
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.servicesimpl.enotegear.Settings.Companion.SYNC_ALL_NOTEBOOKS
import gearsoftware.gearhub.servicesimpl.enotegear.Settings.Companion.SYNC_CONTENT
import gearsoftware.gearhub.servicesimpl.enotegear.Settings.Companion.SYNC_NOTEBOOKS_LIST
import gearsoftware.gearhub.servicesimpl.enotegear.di.ENoteGearModule
import gearsoftware.settings.SettingsBaseFragment
import gearsoftware.settings.base.ISettingsItem
import gearsoftware.settings.base.SettingsCheckItem
import gearsoftware.settings.base.SettingsItem
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject
import toothpick.smoothie.lifecycle.closeOnDestroy


class SettingsFragment : SettingsBaseFragment() {


    private val settings: Settings by inject()

    override fun getSettings(): Map<String, ISettingsItem> =
        mapOf(
            SYNC_CONTENT to SettingsCheckItem(requireContext().getString(R.string.enotegear_sync_content), context?.getString(R.string.enotegear_sync_content_description)!!, settings.syncContent),
            SYNC_ALL_NOTEBOOKS to SettingsCheckItem(requireContext().getString(R.string.enotegear_sync_all_notebooks), "", isChecked = settings.allNotebooks),
            SYNC_NOTEBOOKS_LIST to SettingsItem(requireContext().getString(R.string.enotegear_sync_notebooks)).apply { isVisible = !settings.allNotebooks }
        )

    override fun onItemCheckedChange(key: String, item: SettingsCheckItem, isChecked: Boolean) {
        when (key) {
            SYNC_CONTENT -> settings.syncContent = isChecked
            SYNC_ALL_NOTEBOOKS -> {
                settings.allNotebooks = isChecked
                getSettings()[SYNC_NOTEBOOKS_LIST]?.isVisible = !isChecked
            }
        }
    }

    override fun onItemClick(key: String, item: ISettingsItem) {
        when (key) {
            SYNC_NOTEBOOKS_LIST -> {
                showMultiPickOptions(
                    requireContext(),
                    requireContext().getString(R.string.enotegear_choose_notebooks),
                    settings.syncNotebooks.map { it.name }.toTypedArray(),
                    settings.syncNotebooks.map { it.isChecked }.toBooleanArray(),
                    false
                )
                { booleans ->
                    settings.syncNotebooks.forEachIndexed { index, syncNotebook ->
                        syncNotebook.isChecked = booleans[index]
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KTP.openScope(Scopes.APP)
            .openSubScope(Scopes.ENOTEGEAR)
            .installModules(ENoteGearModule())
            .closeOnDestroy(this)
            .inject(this)
    }
}