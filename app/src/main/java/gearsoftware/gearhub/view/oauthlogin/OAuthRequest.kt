package gearsoftware.gearhub.view.oauthlogin

/**
 * Oauth request data
 */
data class OAuthRequest(
        val clientId: String,
        val siteUrl: String,
        val tokenUrl: String,
        val redirectUrl: String,
        val secretKey: String,
        val scope: String,
        val arguments: String?) {

    fun toTokenExchangeRequest(code : String): String {
        var res: String = String.format("&client_id=%s&client_secret=%s&code=%s", clientId, secretKey, code)
        if (arguments != null) {
            res += "&$arguments"
        }
        return res
    }
}