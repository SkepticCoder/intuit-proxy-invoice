package services.intuit.base

import com.google.inject.ImplementedBy
import play.api.mvc.Cookies



@ImplementedBy(classOf[IntuitAuthorizer])
trait Authorizer {
  def authorize(session: Cookies) : Option[ResultAuthorize]
}
