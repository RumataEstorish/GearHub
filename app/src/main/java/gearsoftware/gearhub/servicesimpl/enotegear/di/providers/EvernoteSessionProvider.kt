package gearsoftware.gearhub.servicesimpl.enotegear.di.providers

import android.content.Context
import com.evernote.client.android.EvernoteSession
import gearsoftware.gearhub.servicesimpl.enotegear.Constants
import toothpick.InjectConstructor
import javax.inject.Provider

@InjectConstructor
class EvernoteSessionProvider(
        private val context: Context
) : Provider<EvernoteSession> {
    override fun get(): EvernoteSession =
            if (EvernoteSession.getInstance() != null) {
                EvernoteSession.getInstance()
            } else {
                EvernoteSession.Builder(context)
                        .setEvernoteService(Constants.EVERNOTE_SERVICE)
                        .setSupportAppLinkedNotebooks(Constants.SUPPORT_APP_LINKED_NOTEBOOKS)
                        .build(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET)
                        .asSingleton()
            }
}