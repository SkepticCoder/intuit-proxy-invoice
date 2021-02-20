package dao.intuit

import com.intuit.ipp.core.Context
import com.intuit.ipp.data.Invoice
import com.intuit.ipp.services.QueryResult
import dao.Repository

import java.time.LocalDate

trait InvoiceRepository extends Repository[Invoice] {

  protected def findByQueryWithResult(query: String)(implicit context: Context): QueryResult

  def findByStartDateBetween(startDate: LocalDate, endDate: LocalDate)(implicit context: Context): Iterable[Invoice]
}
