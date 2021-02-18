package services.intuit.util

import com.intuit.ipp.data.{CustomField, CustomFieldTypeEnum}
import utils.DateUtils.RichDate

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

object IntuitDataUtils {

  val invoiceDatePattern = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  implicit class RichCustomField(customField: CustomField) {

    def getLocalDate: Option[LocalDate] = {
      if (customField.getType == CustomFieldTypeEnum.DATE_TYPE) {
        Option(customField.getDateValue).map(_.toLocalDate)
      } else if (customField.getType == CustomFieldTypeEnum.STRING_TYPE) {
        Option(customField.getStringValue).flatMap(value => Try(LocalDate.parse(value, invoiceDatePattern))
                                          .toOption
                                          .toRight("Invalid Date format.")
                                          .toOption
        )
      } else {
        None
      }
    }
  }
}
