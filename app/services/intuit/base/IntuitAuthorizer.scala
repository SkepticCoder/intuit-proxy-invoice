package services.intuit.base

import play.api.mvc.Cookies
import services.intuit.auth.Cookies.ACCESS_TOKEN_KEY

import javax.inject.{Inject, Singleton}

@Singleton
class IntuitAuthorizer @Inject()(val client: AuthClient, contextProvider: ContextProvider) extends Authorizer {

  override def authorize(cookies: Cookies): Option[ResultAuthorize] = {
    val accessTokenCookie = cookies.get(ACCESS_TOKEN_KEY)

    accessTokenCookie.map(token => SuccessAuthorize(contextProvider.getContext(() => token.value)))
                     .orElse(client.getAuthUrl)
  }
}
