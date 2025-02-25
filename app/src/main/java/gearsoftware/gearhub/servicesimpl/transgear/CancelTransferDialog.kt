package gearsoftware.gearhub.servicesimpl.transgear

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import gearsoftware.gearhub.R


class CancelTransferDialog : AppCompatActivity() {

    companion object {
        const val DIALOG_RESULT = "DIALOG_RESULT"
        const val CANCEL_CLICK = "CANCEL_CLICK"
        const val OK_CLICK = "OK_CLICK"
        const val CANCEL_ONE = "CANCEL_ONE"
        const val CANCEL_ALL = "CANCEL_ALL"
    }

    private var cancelOne = false
    private var cancelAll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transgear_canceldialog_layout)


        cancelOne = intent.getBooleanExtra(CANCEL_ONE, false)
        cancelAll = intent.getBooleanExtra(CANCEL_ALL, false)

        //sendingFile = intent.getStringExtra(SENDING_FILE)
        findViewById<TextView>(R.id.dialogContent).text = if (cancelOne && !cancelAll) {
            resources.getString(R.string.cancel_transfer_single_file) //this.intent.getStringExtra(TransferOneNotification.SENDING_FILE) ?: ""
        } else {
            resources.getString(R.string.cancel_transfer_multiple_files)
        }

        setTitle(R.string.cancel_transfer_dialog_title)

        findViewById<Button>(R.id.dialogNegativeButton).setOnClickListener {
            val sendIntent = Intent(DIALOG_RESULT)
            sendIntent.putExtra(DIALOG_RESULT, CANCEL_CLICK)
            sendBroadcast(sendIntent)
            this@CancelTransferDialog.setResult(RESULT_CANCELED)
            finish()
        }


        findViewById<Button>(R.id.dialogPositiveButton).setOnClickListener {
            val sendIntent = Intent(DIALOG_RESULT)
            sendIntent.putExtra(DIALOG_RESULT, OK_CLICK)
            sendIntent.putExtra(CANCEL_ONE, cancelOne)
            sendIntent.putExtra(CANCEL_ALL, cancelAll)
            sendBroadcast(sendIntent)
            finish()
        }
        window.setGravity(Gravity.BOTTOM)
    }
}
