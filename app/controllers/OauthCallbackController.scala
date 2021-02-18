package controllers

import play.api.Logging
import play.api.mvc.{AbstractController, ControllerComponents, Cookie}
import services.intuit.base.AuthClient

import javax.inject.{Inject, Singleton}

@Singleton
class OauthCallbackController @Inject()(cc: ControllerComponents, authClient: AuthClient) extends AbstractController(cc)
  with Logging {

  /**
   * Handle an authorization code
   *
   * This code may be migrate in AuthAction and parameters could be getting from request
   * @param code
   * @param state
   * @param realmId
   * @param error
   * @param error_description
   * @return
   */
  def handle(code: Option[String], state: Option[String], realmId: Option[String], error: Option[String],
             error_description: Option[String]) = Action { req =>
    code match {
      case Some(authCode) => val bearer = authClient.getAccessToken(authCode)
                             import services.intuit.base.AuthClient._

        Ok.withNewSession.withCookies(bearer.toPairs.map(c => Cookie(
          name = c._1, value = c._2,
          maxAge = Option(bearer.getExpiresIn).map(_.intValue),
          secure = false)): _*)

      case _ => logger.error(s"Error getting auth code $state $error $error_description")
                Unauthorized(error_description.getOrElse(""))
    }
  }

}
