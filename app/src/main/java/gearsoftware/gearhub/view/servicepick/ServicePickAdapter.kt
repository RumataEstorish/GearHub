package gearsoftware.gearhub.view.servicepick

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import gearsoftware.gearhub.R
import gearsoftware.gearhub.serviceprovider.ServiceProxy

class ServicePickAdapter(
    private val onServiceClicked: (ServiceProxy) -> Unit,
    private val onStoreUrlClicked: (storeUrl: String) -> Unit
) : RecyclerView.Adapter<ServicePickAdapter.ServicePickViewHolder>() {

    private lateinit var services: List<ServiceProxy>

    @SuppressLint("NotifyDataSetChanged")
    fun setServices(services: List<ServiceProxy>) {
        this.services = services
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicePickViewHolder =
        ServicePickViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.service_pick_item_layout, parent, false))

    override fun getItemCount(): Int =
        services.size

    override fun onBindViewHolder(holder: ServicePickViewHolder, position: Int) =
        holder.bind(services[position])

    inner class ServicePickViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(service: ServiceProxy) = with(itemView) {

            findViewById<AppCompatImageView>(R.id.appIcon).apply {
                service.icon.clearColorFilter()
                setImageDrawable(service.icon)
            }
            findViewById<TextView>(R.id.appName).text = service.name
            findViewById<TextView>(R.id.appDescription).text = service.description

            findViewById<ConstraintLayout>(R.id.accountPickItemLayout).setOnClickListener { onServiceClicked(service) }
            findViewById<AppCompatImageView>(R.id.storeIcon).setOnClickListener { onStoreUrlClicked(service.storeUrl) }
        }
    }
}