package gearsoftware.gearhub

import android.view.View

@Suppress("unused")
var View.show: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }