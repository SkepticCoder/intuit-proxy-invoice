package services


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import com.intuit.ipp.core.{Context, Response}
import com.intuit.ipp.data.{IntuitResponse, Invoice}
import com.intuit.ipp.serialization.JSONSerializer
import dao.intuit.InvoiceRepository
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, _}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, inject}
import utils.io.withResources

import java.time.LocalDate
import scala.io.Source
import scala.jdk.CollectionConverters.IterableHasAsScala


class InvoiceServiceTest extends PlaySpec
                          with GuiceOneAppPerSuite
                          with BeforeAndAfterAll
                          with MockitoSugar
                          with ArgumentMatchersSugar
                          {

  var invoiceService: InvoiceApi = _
  var repository: InvoiceRepository = _
  implicit var intuitContext: Context = _
  var invoices: Iterable[Invoice] = _



  override def fakeApplication(): Application = {
    repository = mock[InvoiceRepository]
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
          .registerModule(new ParameterNamesModule)
          .registerModule(new Jdk8Module)
          .registerModule(new JavaTimeModule)

    val invoicesStr = withResources(Source.fromResource("invoices.json"))(_.mkString)


    val response: Response = new JSONSerializer().deserialize(invoicesStr, classOf[IntuitResponse])

    invoices = response.asInstanceOf[IntuitResponse]
      .getQueryResponse
      .getIntuitObject
      .asScala
      .map(_.getValue.asInstanceOf[Invoice])

    GuiceApplicationBuilder()
      .overrides(inject.bind[InvoiceRepository].to(repository))
      .build
  }

  var startSubscribeDate: LocalDate = _

  var endSubscribeDate: LocalDate = _


  override protected def beforeAll(): Unit = {
    invoiceService = app.injector.instanceOf(classOf[InvoiceApi])
    intuitContext = mock[Context]

    startSubscribeDate = LocalDate.of(2020, 1, 1)
    endSubscribeDate = startSubscribeDate.plusYears(1)

    // with server filter by Meta.createTime
    when(repository.findByStartDateBetween(argThat[LocalDate]((arg: LocalDate) => arg == null || arg.isAfter(startSubscribeDate)
      || arg.isEqual(startSubscribeDate)
    ), argThat[LocalDate]((arg: LocalDate) => arg == null || arg.isBefore(endSubscribeDate)
      || arg.isEqual(endSubscribeDate)))(same(intuitContext)))
      .thenReturn(invoices)

    when(repository.findByStartDateBetween(*[LocalDate], *[LocalDate])(same(intuitContext)))
      .thenReturn(invoices)
  }

  "getInvoiceBySubscribeDateRange" should {
    "return empty" in {
      val startSubscribeDate = LocalDate.of(2010, 1, 1)
      val endSubscribeDate = startSubscribeDate.plusMonths(3)
      val invoices = invoiceService.getInvoiceBySubscribeDateRange(startSubscribeDate, endSubscribeDate)

      invoices.toArray shouldBe empty
    }

    "return 2 items" in {
      val startSubscribeDate = LocalDate.of(2020, 1, 1)
      val endSubscribeDate = startSubscribeDate.plusYears(3)
      val invoices = invoiceService.getInvoiceBySubscribeDateRange(startSubscribeDate, endSubscribeDate)

      invoices should have size 2
    }
  }



}
