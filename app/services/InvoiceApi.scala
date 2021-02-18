package services

import com.google.inject.ImplementedBy
import com.intuit.ipp.core.Context
import com.intuit.ipp.data.Invoice

import java.time.LocalDate

@ImplementedBy(classOf[InvoiceService])
trait InvoiceApi {
  def getInvoicesByDateRange(startSubscribeDate: LocalDate, endSubscribeDate: LocalDate)
                            (implicit context: Context): Iterable[Invoice]

  def getInvoiceBySubscribeDateRange(startSubscribeDate: LocalDate, endSubscribeDate: LocalDate)
                                    (implicit context: Context): Iterable[Invoice]
}
