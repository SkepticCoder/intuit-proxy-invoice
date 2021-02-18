package model

import com.fasterxml.jackson.annotation.JsonProperty
import play.api.libs.json.{Json, Writes}

import scala.annotation.meta.field

case class Page[T](items: Seq[T],
                   pageIndex: Int = 0,
                   pageSize: Int = 20,
                   totalCount: Long = 20)(
                   implicit val tWrites: Writes[T]) {

  @(JsonProperty @field)("value")
  def getPage = Math.max(1, Math.min(pageIndex, getNumPages))

  @(JsonProperty @field)("value2")
  def getNumPages = 1 + Math.floor((totalCount - 1) / pageSize)
}

object Page {

  def apply[T](items: Seq[T]) (implicit tWrites: Writes[T]): Page[T] = {
    Page(items = items, pageSize = items.size, totalCount = items.size)
  }

  implicit def pageWriter[T]: Writes[Page[T]] = (o: Page[T]) => {
    implicit val tWrites = o.tWrites
    val writes = Json.writes[Page[T]]
    writes.writes(o)
  }
}