package services.intuit.auth.action

import com.intuit.ipp.core.Context
import play.api.mvc._
import services.intuit.base.{Authorizer, NonAnothorize, RedirectAuthorize, SuccessAuthorize}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class AuthRequest[A](ctx: Context, request: Request[A]) extends WrappedRequest[A](request)


class AuthAction @Inject()(authService: Authorizer, parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl[AnyContent](parser) {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    authService.authorize(request.cookies).getOrElse(NonAnothorize()) match {
      case SuccessAuthorize(context) => block(AuthRequest(context, request))
      case RedirectAuthorize(url) => Future.successful(Results.Redirect(url))
      case _ => Future.successful(Results.Unauthorized("Unauthorized access"))
    }
  }


}