package gearsoftware.gearhub.servicesimpl.enotegear

import com.evernote.client.android.EvernoteSession

/**
 * Evernote constants
 */
internal object Constants {
    const val CONSUMER_KEY = "YOUR_KEY_HERE"
    const val CONSUMER_SECRET = "YOUR_SECRET_HERE"
    const val SUPPORT_APP_LINKED_NOTEBOOKS = true
    val EVERNOTE_SERVICE: EvernoteSession.EvernoteService = EvernoteSession.EvernoteService.PRODUCTION
}
