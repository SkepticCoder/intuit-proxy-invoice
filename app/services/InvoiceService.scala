package services


import com.intuit.ipp.core.Context
import com.intuit.ipp.data.Invoice
import config.{AppConfig, AppConfigFactory}
import dao.intuit.InvoiceRepository

import java.time.LocalDate
import javax.inject.{Inject, Singleton}


@Singleton
class InvoiceService @Inject()(val repository: InvoiceRepository, val appConfig: AppConfigFactory) extends InvoiceApi  {

  private val startSubscribeDateFieldName = appConfig.getIntuitConfig.invoiceConfig.startSubscribeDateField
  private val endSubscribeDateFieldName = appConfig.getIntuitConfig.invoiceConfig.endSubscribeDateField

  override def getInvoicesByDateRange(startSubscribeDate: LocalDate, endSubscribeDate: LocalDate)
                                     (implicit context: Context): Iterable[Invoice] = {
    val result = repository.findByStartDateBetween(startSubscribeDate, endSubscribeDate)
    result.toSeq
  }

  override def getInvoiceBySubscribeDateRange(startSubscribeDate: LocalDate, endSubscribeDate: LocalDate)
                                             (implicit context: Context): Iterable[Invoice] = {
    val result = getInvoicesByDateRange(startSubscribeDate, endSubscribeDate)
      .filter(isSubscribeDateInRange(_, startSubscribeDate, endSubscribeDate))
    result.toSeq
  }

  private def isSubscribeDateInRange(invoice: Invoice, startSubscribeDate: LocalDate, endSubscribeDate: LocalDate):
  Boolean = {

    import services.intuit.util.IntuitDataUtils._
    import utils.DateUtils._

    getCustomFieldByName(invoice, startSubscribeDateFieldName).
      zip(getCustomFieldByName(invoice, endSubscribeDateFieldName)) match {
      case Some((fieldStartSubscribe, fieldEndSubscribe)) =>
        fieldStartSubscribe.getLocalDate.exists(_.isInRange(Some(startSubscribeDate), Some(endSubscribeDate))) &&
        fieldEndSubscribe.getLocalDate.exists(_.isInRange(Some(startSubscribeDate), Some(endSubscribeDate)))

      case _ => false
    }
  }

  private def getCustomFieldByName(invoice: Invoice, name: String) = {
    import scala.jdk.CollectionConverters.CollectionHasAsScala
    invoice.getCustomField.asScala.find(_.getName == name)
  }
}
