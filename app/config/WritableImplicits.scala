package config

import model.Page
import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Codec

trait WritableImplicits {
  implicit def jsonWritable[A](implicit writes: Writes[A], codec: Codec): Writeable[A] = {
    implicit val contentType = ContentTypeOf[A](Some(ContentTypes.JSON))
    val transform = Writeable.writeableOf_JsValue.transform compose (writes.writes _)
    Writeable(transform)
  }

  implicit def pageWriter[T: Writes](): Writes[Page[T]] = Json.writes[Page[T]]

}

object WritableImplicits extends WritableImplicits
