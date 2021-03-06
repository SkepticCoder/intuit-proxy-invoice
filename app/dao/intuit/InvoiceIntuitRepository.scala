package dao.intuit

import com.intuit.ipp.core.Context
import com.intuit.ipp.data.Invoice
//import io.getquill.{Embedded, Literal, MirrorSqlDialect, SqlMirrorContext}
import services.intuit.base.DataServiceProvider

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

@Singleton
class InvoiceIntuitRepository @Inject()(dataServiceProvider: DataServiceProvider)
  extends IntuitRepository[Invoice](dataServiceProvider) with InvoiceRepository  {

  val querySelectInvoiceByCreateTime = (startDate: LocalDate, endDate: LocalDate) =>
    s"""SELECT *
        FROM Invoice
        WHERE MetaData.CreateTime >= '${startDate.toString.replace("'", "''")}' and
              MetaData.CreateTime < '${endDate.toString.replace("'", "''")}'
    """

//  val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)

  override def findByStartDateBetween(startDate: LocalDate, endDate: LocalDate)(implicit context: Context): Iterable[Invoice] = {
/*    import ctx._
    val q = quote { InvoiceSchema().filter(i => i.metaData.createTime = lift(startDate)).map(_.id) }

    val queryResult = ctx.run(q)

    val sourceQueryStr = queryResult.string(true)
    val resultQuery =  sourceQueryStr.replace("?", "'" + startDate.toString.replace("'", "''") + "'")
    logger.debug(s"Executing query.. Unbound paramerets: $sourceQueryStr, bound paremetesrs " +
      s"$resultQuery" )*/

    InvoiceIntuitRepository.super.executeQuery(querySelectInvoiceByCreateTime(startDate, endDate))
  }
}
