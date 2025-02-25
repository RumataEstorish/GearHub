package gearsoftware.gearhub.view.oauthlogin

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.google.gson.Gson
import gearsoftware.gearhub.R
import gearsoftware.gearhub.di.Scopes
import gearsoftware.gearhub.services.data.model.LoginResultStatus
import gearsoftware.sap.utils.bodyEx
import gearsoftware.sap.utils.toMediaTypeOrNull
import gearsoftware.sap.utils.toRequestBody
import okhttp3.*
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject
import java.io.IOException
import java.net.URLDecoder
import java.security.SecureRandom


class OAuthLoginActivity : ServiceLoginActivity() {

    companion object {
        const val OAUTH_REQUEST = "OAUTH_REQUEST"
        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android %s; %s %s) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Mobile Safari/537.36"
    }

    private val client: OkHttpClient by inject<OkHttpClient>()
    private var call: Call? = null
    private lateinit var request: OAuthRequest

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KTP.openScope(Scopes.APP)
                .inject(this)

        val rand = SecureRandom()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.login_layout)

        val browser: WebView = findViewById(R.id.webView)
        browser.clearCache(true)
        browser.clearFormData()
        browser.clearHistory()
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        @Suppress("DEPRECATION")
        cookieManager.removeAllCookie()

        val progressBar: ProgressBar = findViewById(R.id.load_progress)

        browser.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE

        val i = intent
        if (i.hasExtra(OAUTH_REQUEST)) {
            request = Gson().fromJson(i.getStringExtra(OAUTH_REQUEST), OAuthRequest::class.java)
        }

        browser.settings.javaScriptEnabled = true
        browser.settings.domStorageEnabled = true
        browser.settings.databaseEnabled = true
        browser.settings.javaScriptCanOpenWindowsAutomatically = true
        browser.settings.userAgentString = String.format(USER_AGENT, Build.VERSION.RELEASE, Build.MANUFACTURER, Build.MODEL)

        browser.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (url != null) {
                    if (!url.contains(request.redirectUrl)) {
                        browser.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    } else {
                        browser.visibility = View.INVISIBLE
                        progressBar.visibility = View.VISIBLE
                    }
                }
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (url.contains(request.redirectUrl)) {
                    browser.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    processToken(url, request.redirectUrl)
                }
                super.onPageFinished(view, url)
            }
        }

        if (!::request.isInitialized) {
            finish()
            return
        }
        browser.loadUrl(String.format("%1s?response_type=code&client_id=%2s&scope=%3s&state=%4s", request.siteUrl, request.clientId, request.scope, rand.nextInt()))
    }

    private fun retrieveToken(code: String) {

        if (call != null) {
            return
        }


        val req = Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(this.request.tokenUrl)
                .post(this.request.toTokenExchangeRequest(code).toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())).build()


        call = client.newCall(req)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                sendLoginResult(serviceName, 0, RESULT_CANCELED, null, "", LoginResultStatus.RETRY)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.bodyEx
                val bodyString = body?.string()
                if (bodyString.isNullOrEmpty()) {
                    sendLoginResult(serviceName, 0, RESULT_CANCELED, null, "", LoginResultStatus.RETRY)
                    return
                }
                sendLoginResult(serviceName, 0, RESULT_OK, null, bodyString, status = LoginResultStatus.SUCCESS)
            }
        })
    }

    private fun processToken(url: String?, redirectUrl: String?) {
        val decoded: String
        if (url == null) {
            return
        }

        if (url.startsWith(redirectUrl!!)) {
            decoded = try {
                URLDecoder.decode(url, "UTF-8")
            } catch (ex: Exception) {
                url
            }
            var code = decoded.substring(decoded.indexOf("code=") + 5)
            val index = code.indexOf('&')
            if (index != -1) {
                code = code.substring(0, index)
            }
            retrieveToken(code)
        }
    }
}
