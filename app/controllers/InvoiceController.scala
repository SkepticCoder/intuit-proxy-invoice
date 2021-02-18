package controllers


import config.WritableImplicits._
import model.{InvoiceDto, Page}
import org.apache.commons.lang3.builder.{MultilineRecursiveToStringStyle, ToStringBuilder}
import play.api.mvc._
import services.InvoiceApi
import services.intuit.auth.action.{AuthAction, AuthRequest}

import java.time.LocalDate
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller create an action to invoices
 */
@Singleton // or object if no DI
class InvoiceController @Inject()(cc: ControllerComponents,
                                  invoiceService: InvoiceApi,
                                  auth: AuthAction
                                 )(implicit exec: ExecutionContext) extends AbstractController(cc) {


  /**
   * Create an action that responds with the [[Counter]]'s current
   * count. The result is plain text. This `Action` is mapped to
   * `GET /count` requests by an entry in the `routes` config file.
   */
  def getInvoices(startSubscribeDate: LocalDate, endSubscribeDate: LocalDate) = auth.async {
    implicit request =>
      Future {
        {
          // I know that the downcasting is the bad practise, but i couldn't compose action or create custom body parser
          val authRequest = request.asInstanceOf[AuthRequest[AnyContent]]
          implicit val ctx = authRequest.ctx

          val result = invoiceService.getInvoiceBySubscribeDateRange(startSubscribeDate, endSubscribeDate)

          render {
            case Accepts.Html() =>
              Ok(result.map(ToStringBuilder.reflectionToString(_,
                new MultilineRecursiveToStringStyle())).mkString("\n"))

            case Accepts.Json() =>
              import InvoiceDto._
              import Page._
              Ok(Page[InvoiceDto](result.map(invoiceToInvoiceDto).toSeq))
          }
        }
      }
  }

}
