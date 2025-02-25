package gearsoftware.gearhub.view.servicelist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gearsoftware.gearhub.R
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.di.ServiceListModule
import gearsoftware.gearhub.serviceprovider.ServiceProxy
import gearsoftware.gearhub.view.MarginItemDecoration
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject


/**
 * Account list fragment
 */
class ServiceListFragment : Fragment(), IServiceListView {

    companion object {
        const val TAG = "LIST_FRAGMENT"

        @JvmStatic
        fun getInstance(): ServiceListFragment = ServiceListFragment()
    }

    private val presenter: IServiceListPresenter by inject()
    private val arrayAdapter: ServiceListAdapter by lazy {
        ServiceListAdapter { url ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.service_list_fragment, container, false)

        KTP.openScopes(Scopes.APP)
            .openSubScope(this)
            .installModules(ServiceListModule(this))
            .inject(this)

        view.findViewById<RecyclerView>(R.id.accountsList).apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(MarginItemDecoration(requireContext().resources.getDimensionPixelSize(R.dimen.samsung_margin_half)))
            adapter = arrayAdapter
        }

        arrayAdapter.onLoginClicked
            .subscribeBy(
                onNext = presenter::onLoginClick,
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        arrayAdapter.onLogoutClicked
            .subscribeBy(
                onNext = presenter::onLogoutClick,
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        arrayAdapter.onServiceRemoved
            .subscribeBy(
                onNext = presenter::onServiceRemoveClick,
                onError = Timber::e
            )
            .addTo(compositeDisposable)

        arrayAdapter.onSettingsClicked
            .subscribeBy(
                onNext = presenter::onSettingsClick,
                onError = Timber::e
            )
            .addTo(compositeDisposable)


        return view
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun update(services: List<ServiceProxy>) {
        arrayAdapter.setServices(services)
    }
}