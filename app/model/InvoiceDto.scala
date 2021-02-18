package model

import com.intuit.ipp.data.Invoice
import play.api.libs.json.{Json, JsonConfiguration, OptionHandlers}

import java.time.{LocalDateTime, ZoneOffset}


case class MetaData(createTime: LocalDateTime)

object MetaData {
  implicit val config = JsonConfiguration(optionHandlers = OptionHandlers.Default)

  implicit val metadataWriter = Json.writes[MetaData]
}

// class Invoice doesn't have any constructors
case class InvoiceDto(id: String, deposit: Option[BigDecimal], invoiceLink: Option[String], status: Option[String]/*, metadata: MetaData*/)

object InvoiceDto {
  implicit val config = JsonConfiguration(optionHandlers = OptionHandlers.Default)

  implicit val invoiceWriter = Json.writes[InvoiceDto]

  implicit def invoiceToInvoiceDto(invoice: Invoice) = InvoiceDto(
    id = invoice.getId,
    deposit = Option(invoice.getDeposit),
    invoiceLink = Option(invoice.getInvoiceLink),
    status = Option(invoice.getStatus).map(_.value)
/*
    metadata = Option(invoice.getMetaData).map(metaData =>
      MetaData(LocalDateTime.ofInstant(metaData.getCreateTime.toInstant, ZoneOffset.UTC)))
      .orNull*/
  )
}

