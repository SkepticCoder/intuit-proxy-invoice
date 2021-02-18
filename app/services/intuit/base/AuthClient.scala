package services.intuit.base

import com.intuit.oauth2.client.OAuth2PlatformClient
import com.intuit.oauth2.config.Scope
import com.intuit.oauth2.data.BearerTokenResponse
import config.AppConfigFactory
import play.api.Logging
import play.api.libs.ws.WSClient
import services.intuit.auth.Cookies.{ACCESS_TOKEN_KEY, EXPIRES_TIME, REFRESH_TOKEN_KEY}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Await
import scala.concurrent.duration.Duration

@Singleton
class AuthClient @Inject()(contextProvider: ContextProvider, appConfigFactory: AppConfigFactory,
                           wsClient: WSClient) extends Logging {

  val oathClient = new OAuth2PlatformClient(appConfigFactory.getOathConfig)


  def getAuthUrl: Option[ResultAuthorize] = {
    Some(RedirectAuthorize(constructRedirectUrl))
  }

  def getAccessToken(authCode: String) = {
    oathClient.retrieveBearerTokens(authCode, "http://localhost:9090/oathredirect")
  }

  private def constructRedirectUrl = {
    val scopes = List(Scope.OpenId, Scope.Accounting, Scope.Profile)
    import collection.JavaConverters._

    appConfigFactory.getOathConfig.prepareUrl(scopes.asJava, "http://localhost:9090/oathredirect")
  }

  def authorizeWithoutBrowser = {
    val appConfig = appConfigFactory.getIntuitConfig

    //Generate the CSRF token
    val csrf = appConfigFactory.getOathConfig.generateCSRFToken

    val scopes = List(Scope.All)

    import collection.JavaConverters._
    val url = appConfigFactory.getOathConfig.prepareUrl(scopes.asJava, "http://localhost", csrf)
    val response = Await.result(wsClient.url(url).get(), Duration.Inf)
    val response2 = Await.result(wsClient
      .url(response.uri.toURL.toString)
      .withFollowRedirects(true)
      .post("{username: \"mail@gmail.com\", password: \"some password\", namespaceId: \"50000003\"}"),
      Duration.Inf)

    val client = new OAuth2PlatformClient(appConfigFactory.getOathConfig)
    //client.retrieveBearerTokens()
    client.refreshToken("").getAccessToken
  }

}

object AuthClient {
  implicit class RichBearerTokenResponse(val bearerToken: BearerTokenResponse) {

    def toPairs() = {
      Seq(ACCESS_TOKEN_KEY -> bearerToken.getAccessToken,
        REFRESH_TOKEN_KEY -> bearerToken.getRefreshToken,
        EXPIRES_TIME -> bearerToken.getExpiresIn.toString)
    }
  }
}
