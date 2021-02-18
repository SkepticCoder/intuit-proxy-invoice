package config

import play.api.mvc.{PathBindable, QueryStringBindable}

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

object Binders {

  val sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  implicit object bindableDate extends QueryStringBindable.Parsing[LocalDate] (
    LocalDate.parse(_, sdf), _.toString, (k: String, e: Exception) =>
      "Cannot parse %s as Date: %s".format(k, e.getMessage)
  )

  implicit def bindableDate(implicit stringBinder: PathBindable[String]) = new PathBindable[LocalDate] {


    override def bind(key: String, value: String): Either[String, LocalDate] = {
      for {
        dateString <- stringBinder.bind(key, value).right
        date <- Try(LocalDate.parse(dateString, sdf)).toOption.toRight("Invalid Date format.").right
      } yield date
    }

    override def unbind(key: String, date: LocalDate): String = key + "=" + sdf.format(date)

  }

}
