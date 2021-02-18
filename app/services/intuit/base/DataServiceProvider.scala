package services.intuit.base

import com.intuit.ipp.core.Context
import com.intuit.ipp.data.Invoice
import com.intuit.ipp.services.DataService
import com.intuit.oauth2.client.OAuth2PlatformClient
import com.intuit.oauth2.config.OAuth2Config.OAuth2ConfigBuilder
import com.intuit.oauth2.config.{Environment, Scope}
import config.AppConfigFactory
import play.api.Logging
import play.api.libs.ws.WSClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.Await
import scala.concurrent.duration.Duration

@Singleton
class DataServiceProvider @Inject()(contextProvider: ContextProvider) extends Logging {
  def getDataService()(implicit context: Context) = {
    new DataService(context)
  }
}
