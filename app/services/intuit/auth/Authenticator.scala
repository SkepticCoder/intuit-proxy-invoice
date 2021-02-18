package services.intuit.auth

import play.mvc.{Http, Security}

import java.util.Optional

class Authenticator extends Security.Authenticator {
  /**
   * Get Acces token
   * @param request
   * @return Access token
   */
  override def getUsername(req: Http.Request): Optional[String] = {
    Optional.of("test")
  }
}
