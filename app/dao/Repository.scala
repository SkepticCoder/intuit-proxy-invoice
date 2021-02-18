package dao

import com.intuit.ipp.core.Context

trait Repository[T] {

  def findAll(implicit context: Context): Iterable[T]

  def executeQuery(query: String)(implicit context: Context): Iterable[T]
}

object Repository {
  case class Field(name: String, value: String)
}
