package dao.intuit

import com.intuit.ipp.core.{Context, IEntity}
import dao.Repository
import play.api.Logging
import services.intuit.base.DataServiceProvider

import scala.collection.mutable
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.reflect.ClassTag

abstract class IntuitRepository[T <: IEntity : ClassTag](dataService: DataServiceProvider) extends Repository[T] with Logging{

  val instanceType = newInstance[T]

  private def newInstance[T: ClassTag] = implicitly[ClassTag[T]].runtimeClass.newInstance.asInstanceOf[T]


  override def findAll(implicit context: Context) = {
    dataService.getDataService().findAll(newInstance[T]).asScala // I didn't use async mode because this client don't polling threads
  }

  override def executeQuery(query: String)(implicit context: Context) = {
    val list = mutable.ListBuffer[T]()
    findByQueryWithResult(query).getEntities.asScala.foreach(item => list.addOne(item.asInstanceOf[T]))
    list
  }

  protected def findByQueryWithResult(query: String)(implicit context: Context) = {
    dataService.getDataService().executeQuery(query)
  }
}
