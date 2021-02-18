package utils

import java.time.{LocalDate, LocalDateTime}
import java.util.Date

object DateUtils {

  implicit class RichDate(date: Date) {

    def toLocalDate = {
      Option(date).map(date => LocalDate.from(date.toInstant)).orNull
    }

    def toLocalDateTime = {
      Option(date).map(date => LocalDateTime.from(date.toInstant)).orNull
    }
  }

  implicit class InDate(localDate: LocalDate) {
    def isInRange(lowerBoundOp: Option[LocalDate], upperBoundOp: Option[LocalDate]): Boolean = {
      // if doesnt set bounds that lowerBoundOp.forall(isAfterOrEqual) && upperBoundOp.forall(isAfterOrEqual)
      lowerBoundOp.exists(isAfterOrEqual) && upperBoundOp.exists(isBeforeOrEqual)
    }

    def isAfterOrEqual(lowerBound: LocalDate) = {
      localDate.isEqual(lowerBound) || localDate.isAfter(lowerBound)
    }

    def isBeforeOrEqual(upperBound: LocalDate) = {
      localDate.isEqual(upperBound) || localDate.isBefore(upperBound)
    }
  }

}
