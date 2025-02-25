package gearsoftware.gearhub.view.servicelist

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import gearsoftware.gearhub.R
import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.serviceprovider.ServiceWebProxy
import io.reactivex.rxjava3.subjects.PublishSubject

class ServiceListAdapter(
    private val onStoreClick: (url: String)->Unit
) : RecyclerView.Adapter<ServiceListAdapter.ServiceViewHolder>() {

    private var services: List<ServiceProxy> = listOf()

    val onServiceRemoved: PublishSubject<ServiceProxy> = PublishSubject.create()
    val onSettingsClicked: PublishSubject<ServiceProxy> = PublishSubject.create()
    val onLoginClicked: PublishSubject<ServiceProxy> = PublishSubject.create()
    val onLogoutClicked: PublishSubject<ServiceProxy> = PublishSubject.create()

    fun setServices(services: List<ServiceProxy>) {
        this.services = services
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder =
        ServiceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.service_list_item_layout, parent, false))

    override fun getItemCount(): Int =
        services.size

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) =
        holder.bind(services[position])

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(service: ServiceProxy) {
            val serviceWebProvider = service as? ServiceWebProxy
            val appIcon: AppCompatImageView = itemView.findViewById(R.id.appIcon)
            val userName: TextView = itemView.findViewById(R.id.userName)
            val loginButton: Button = itemView.findViewById(R.id.loginButton)
            val logoutButton: Button = itemView.findViewById(R.id.logoutButton)
            val settingsButton: Button = itemView.findViewById(R.id.settingsButton)
            val storeIcon: AppCompatImageView = itemView.findViewById(R.id.storeIcon)

            itemView.findViewById<TextView>(R.id.appName).text = service.name
            itemView.findViewById<TextView>(R.id.appDescription).text = service.description
            appIcon.setImageDrawable(service.icon)

            itemView.findViewById<Button>(R.id.removeButton).setOnClickListener {
                val builder = AlertDialog.Builder(itemView.context, R.style.ActivityAlertDialogOneUiStyle)
                val dialog = builder.create()
                dialog.setTitle(android.R.string.dialog_alert_title)
                dialog.setMessage(
                    if (serviceWebProvider?.isLoggedIn == true) {
                        itemView.context.getString(R.string.remove_webprovider_confirm)
                    } else {
                        itemView.context.getString(R.string.remove_confirm)
                    }
                )

                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, itemView.context.getString(android.R.string.cancel)) { _, _ -> dialog.dismiss() }
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, itemView.context.getString(android.R.string.ok)) { _, _ -> onServiceRemoved.onNext(service) }
                dialog.window?.setGravity(Gravity.BOTTOM)
                dialog.show()
            }

            if (serviceWebProvider != null && serviceWebProvider.userName.isNotEmpty()) {
                userName.text = (String.format("%1\$s: %2\$s", itemView.context.getString(R.string.account), serviceWebProvider.userName))
                userName.visibility = View.VISIBLE
            } else {
                userName.visibility = View.GONE
                loginButton.visibility = View.GONE
                logoutButton.visibility = View.GONE
            }

            if (service.haveSettings && service is ServiceWebProxy && service.isLoggedIn) {
                settingsButton.setOnClickListener { onSettingsClicked.onNext(service) }
                settingsButton.visibility = View.VISIBLE
            } else {
                settingsButton.visibility = View.GONE
            }


            serviceWebProvider?.let {
                if (!serviceWebProvider.isLoggedIn) {
                    loginButton.setOnClickListener { onLoginClicked.onNext(service) }
                    val matrix = ColorMatrix()
                    matrix.setSaturation(0f)
                    val filter = ColorMatrixColorFilter(matrix)
                    appIcon.colorFilter = filter
                    loginButton.visibility = View.VISIBLE
                    logoutButton.visibility = View.GONE
                    storeIcon.setImageResource(R.drawable.galaxy_store_gray)
                } else {
                    logoutButton.setOnClickListener { onLogoutClicked.onNext(service) }
                    loginButton.visibility = View.GONE
                    logoutButton.visibility = View.VISIBLE
                    appIcon.clearColorFilter()
                    service.icon.clearColorFilter()
                    storeIcon.setImageResource(R.drawable.galaxy_store_color)
                }
            } ?: run {
                appIcon.clearColorFilter()
                service.icon.clearColorFilter()
                storeIcon.setImageResource(R.drawable.galaxy_store_color)
                loginButton.visibility = View.GONE
                logoutButton.visibility = View.GONE
            }

            storeIcon.setOnClickListener { onStoreClick(service.storeUrl) }
        }
    }
}