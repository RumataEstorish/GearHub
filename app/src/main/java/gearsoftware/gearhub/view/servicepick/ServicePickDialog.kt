package gearsoftware.gearhub.view.servicepick

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gearsoftware.gearhub.R
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.services.ServicesManager
import gearsoftware.gearhub.view.MarginItemDecoration
import io.reactivex.rxjava3.subjects.PublishSubject
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject


class ServicePickDialog : DialogFragment() {

    companion object {
        const val TAG = "SERVICE_PICK_DIALOG"

        @JvmStatic
        fun createDialog(): ServicePickDialog =
            ServicePickDialog()
    }

    private val servicePickAdapter: ServicePickAdapter by lazy {
        ServicePickAdapter(onServiceClicked = { serviceProxy ->
            servicesChanged.onNext(listOf(serviceProxy))
            dismiss()
        }, onStoreUrlClicked = {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            startActivity(browserIntent)
            dismiss()
        })
    }

    private val servicesManager: ServicesManager by inject()
    val servicesChanged: PublishSubject<List<ServiceProxy>> = PublishSubject.create()


    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        KTP.openScope(Scopes.APP)
            .inject(this)

        val builder = AlertDialog.Builder(requireContext(), R.style.PickServiceDialogStyle)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.service_pick_dialog_layout, null)



        servicePickAdapter.setServices(servicesManager.availableProviders)

        view.findViewById<RecyclerView>(R.id.serviceList).apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(MarginItemDecoration(requireContext().resources.getDimensionPixelSize(R.dimen.samsung_margin_half)))
            adapter = servicePickAdapter
        }

        builder.setTitle(R.string.choose_services)
        builder.setView(view)
        /*builder.setPositiveButton(android.R.string.ok) { _, _ ->
            servicesChanged.onNext(servicePickAdapter.changedProviders)
        }*/
        builder.setNegativeButton(android.R.string.cancel) { _, _ -> }

        return builder.create().apply { window?.setGravity(Gravity.BOTTOM) }
    }
}