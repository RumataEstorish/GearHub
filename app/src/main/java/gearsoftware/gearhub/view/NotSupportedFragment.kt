package gearsoftware.gearhub.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gearsoftware.gearhub.R

/**
 * Accessory not supported fragment
 */
class NotSupportedFragment : androidx.fragment.app.Fragment() {

    companion object {
        const val TAG = "NOT_SUPPORTED_FRAGMENT"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.not_supported_layout, container, false)
}
