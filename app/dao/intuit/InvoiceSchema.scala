package dao.intuit

import io.getquill.{Embedded, Literal, MirrorSqlDialect, SqlMirrorContext}
import io.getquill.mirrorContextWithQueryProbing.{querySchema, quote}

import java.time.LocalDate

object InvoiceSchema {

  case class Invoice(id: String, metaData: MetaData)

  case class MetaData(createTime: LocalDate) extends Embedded

  def apply() = {
    quote {
      querySchema[Invoice]("Invoice", _.metaData.createTime -> "Metadata.CreateTime")
    }
  }
}
