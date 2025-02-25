package gearsoftware.gearhub.serviceprovider

interface IAccessTokenRepository {
    var accessToken: String
    val isLoggedIn: Boolean
}
