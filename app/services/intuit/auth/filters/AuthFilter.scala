package services.intuit.auth.filters

import akka.stream.Materializer
import play.api.libs.typedmap.TypedKey

import javax.inject._
import play.api.mvc._
import services.intuit.auth.Authenticator
import services.intuit.auth.action.AuthRequest
import services.intuit.base.{Authorizer, NonAnothorize, RedirectAuthorize, SuccessAuthorize}

import scala.concurrent.{ExecutionContext, Future}

/**
 * This filter for authorization
 *
 * @param mat  This object is needed to handle streaming of requests
 *             and responses.
 * @param exec This class is needed to execute code asynchronously.
 *             It is used below by the `map` method.
 */
@Singleton
class AuthFilter @Inject()(
                            implicit override val mat: Materializer,
                            auth: Authorizer,
                            exec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {

    auth.authorize(requestHeader.cookies).getOrElse(NonAnothorize()) match {
      case successAuthorize: SuccessAuthorize =>
        import services.intuit.auth.filters.AuthFilter.RichRequestHeader
        nextFilter(requestHeader.addResultAuthorize(successAuthorize))

      case RedirectAuthorize(url) => Future.successful(Results.Redirect(url))
      case _ => Future.successful(Results.Unauthorized("Unauthorized access"))
    }
  }
}

object AuthFilter {

  val RESULT_AUTHORIZE_ATTR = "authorize"

  implicit class RichRequestHeader(requestHeader: RequestHeader) {
    def addResultAuthorize(resultAuthorize: SuccessAuthorize) = {
      requestHeader.addAttr(TypedKey(RESULT_AUTHORIZE_ATTR), resultAuthorize)
    }
  }

}