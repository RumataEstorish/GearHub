package gearsoftware.gearhub.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.samsung.android.sdk.SsdkUnsupportedException
import com.samsung.android.sdk.accessory.SA
import gearsoftware.gearhub.BaseActivity
import gearsoftware.gearhub.R
import gearsoftware.gearhub.di.MainActivityModule
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.serviceprovider.AbstractSapService.Companion.FOREGROUND_OPEN_LINK_ID
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider.Companion.NOTIFICATION_EXTRA
import gearsoftware.gearhub.serviceprovider.SapNotificationProvider.Companion.OPEN_LINK_ACTION
import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.services.SapPermission
import gearsoftware.gearhub.view.InviteFragment
import gearsoftware.gearhub.view.NotSupportedFragment
import gearsoftware.gearhub.view.main.model.PermissionType
import gearsoftware.gearhub.view.servicelist.ServiceListFragment
import gearsoftware.gearhub.view.servicepick.ServicePickDialog
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject
import java.util.*

class MainActivity : BaseActivity(), IMainView {

    companion object {
        private const val PERMISSION_REQUEST = 333
    }

    private lateinit var fab: FloatingActionButton
    private val compositeDisposable = CompositeDisposable()
    private val accountListFragment: ServiceListFragment by lazy {
        ServiceListFragment.getInstance()
    }
    private val presenter: IMainPresenter by inject()


    override fun requestPermission(permissions: List<String>) {
        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST)
    }

    override fun showPermissionDialog(sapPermission: SapPermission) {
        if (sapPermission.title.isNotBlank() && sapPermission.description.isNotBlank()) {
            val dialog = AlertDialog.Builder(this, R.style.ActivityAlertDialogOneUiStyle)
                .setTitle(sapPermission.title)
                .setMessage(sapPermission.description)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    presenter.onPermissionDialogOk(sapPermission)
                }
                .create()

            if (!sapPermission.required) {
                dialog.setButton(
                    AlertDialog.BUTTON_NEGATIVE,
                    getString(android.R.string.cancel)
                ) { _, _ ->
                    run {
                        presenter.onPermissionDialogCancel(sapPermission)
                        dialog.dismiss()
                    }
                }
            }
            dialog.window?.setGravity(Gravity.BOTTOM)
            dialog.show()
        }
    }

    override fun checkPermission(sapPermission: SapPermission): Boolean =
        if (sapPermission.permissions.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION) && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (sapPermission.permissions.size > 1) {
                sapPermission.permissions.all {
                    if (it != Manifest.permission.ACCESS_BACKGROUND_LOCATION) {
                        ContextCompat.checkSelfPermission(
                            this,
                            it
                        ) == PackageManager.PERMISSION_GRANTED
                    } else {
                        true
                    }
                }
            } else {
                true
            }
        } else {
            sapPermission.permissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
        }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val notGrantedPermissions = LinkedList<String>()
        for (j in permissions.indices) {
            if (grantResults[j] != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(permissions[j])
            }
        }
        presenter.onPermissionsNotGranted(notGrantedPermissions)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun redirectToSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun showPermissionNotGranted(name: String) {
        Toast.makeText(
            this,
            String.format(
                "%s %s",
                name,
                resources.getString(R.string.service_removed_permission_not_granted)
            ),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showPermissionNotGrantedDialog(
        service: ServiceProxy,
        permissionType: PermissionType
    ) {
        val dialog = AlertDialog.Builder(this, R.style.ActivityAlertDialogOneUiStyle)
            .apply {
                title = service.name
                setMessage(
                    context.getString(
                        R.string.permission_required, service.name, when (permissionType) {
                            PermissionType.LOCATION -> getString(R.string.locatiion_permission)
                            PermissionType.STORAGE -> getString(R.string.storage_permission)
                        }
                    )
                )
            }.create()

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.settings)) { _, _ ->
            redirectToSettings()
            presenter.onGoToSettingsDialog(service)
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.remove_service)) { _, _ ->
            presenter.onRemoveServiceDialog(service)
        }
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()
    }

    override fun checkSASupport(): Boolean {
        val mAccessory = SA()
        return try {
            mAccessory.initialize(this)
            mAccessory.isFeatureEnabled(SA.DEVICE_ACCESSORY)
        } catch (e: SsdkUnsupportedException) {
            false
        }

    }

    override fun showAddButton(show: Boolean) {
        if (show) {
            fab.show()
        } else {
            fab.hide()
        }
    }

    private fun changeFragment(fragment: Fragment?, tag: String) {
        val fragmentManager = supportFragmentManager
        val fr = fragmentManager.findFragmentByTag(tag)
        if (fr != null && fr.isVisible) {
            return
        }
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentLayout, fragment!!, tag)
        transaction.commitAllowingStateLoss()
        fragmentManager.executePendingTransactions()
    }

    override fun showNotSupported() {
        changeFragment(NotSupportedFragment(), NotSupportedFragment.TAG)
    }

    override fun showInvite() {
        changeFragment(InviteFragment(), InviteFragment.TAG)
    }

    override fun showList() {
        changeFragment(accountListFragment, ServiceListFragment.TAG)
    }

    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab = findViewById(R.id.fab)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        KTP.openScopes(Scopes.APP)
            .openSubScope(this)
            .installModules(MainActivityModule(this))
            .inject(this)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermission = SapPermission(
                listOf(Manifest.permission.POST_NOTIFICATIONS),
                getString(R.string.notification_permission_title),
                getString(R.string.notification_permission_description),
                true
            )

            if (shouldShowRequestPermissionRationale(notificationPermission.permissions.first())) {
                if (!checkPermission(notificationPermission)) {
                    showPermissionDialog(
                        notificationPermission
                    )
                }
            }
        }

        fab.setOnClickListener { presenter.onAddService() }

        removeApp("petbotproject.enotegear")
        removeApp("petbotproject.socialgear")
        removeApp("com.petbotproject.squaregear")
        removeApp("com.petbotproject.transgear")
        //removeApp("com.petbotproject.walletgear");

        when (intent.action) {
            OPEN_LINK_ACTION -> presenter.onOpenLink(intent.getStringExtra(NOTIFICATION_EXTRA))
        }

        presenter.onCreated()
    }

    private fun checkIgnoreBatteryOptimization() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent()
                val packageName = packageName
                val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

                if (!pm.isIgnoringBatteryOptimizations(packageName)) {

                    val dialog = AlertDialog.Builder(this, R.style.ActivityAlertDialogOneUiStyle)
                        .setMessage(R.string.battery_optimization_warning)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                            intent.data = Uri.parse("package:$packageName")
                            startActivity(intent)
                        }
                        .create()
                    dialog.window?.setGravity(Gravity.BOTTOM)
                    dialog.show()
                }
            }
        } catch (ignored: Exception) {

        }
    }

    override fun openLink(address: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }

        intent.resolveActivity(packageManager)?.let {
            startActivity(intent)
        }
    }

    override fun closeOpenLinkNotification() {
        NotificationManagerCompat.from(this).cancel(FOREGROUND_OPEN_LINK_ID)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
        compositeDisposable.clear()
    }

    private fun removeApp(packageName: String) {
        if (isAppInstalled(packageName)) {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        } catch (ignored: PackageManager.NameNotFoundException) {
            return false
        }

        return true
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_more_apps -> {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.sites.google.com/view/gearsoftware/applications")
                    )
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        getString(R.string.browser_not_found) + ": " + "https://www.sites.google.com/view/gearsoftware/applications",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showProvidersSelection() {
        val dialog = ServicePickDialog.createDialog()

        dialog.servicesChanged
            .subscribeBy(
                onNext = presenter::onServicesAdded,
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        dialog.show(supportFragmentManager, ServicePickDialog.TAG)
    }


    override fun onResume() {
        super.onResume()
        checkIgnoreBatteryOptimization()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }


}