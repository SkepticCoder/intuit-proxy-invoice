package config

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import com.intuit.oauth2.config.Environment
import com.intuit.oauth2.config.OAuth2Config.OAuth2ConfigBuilder
import com.typesafe.config.{Config, ConfigFactory}
import play.api.ConfigLoader

import javax.inject.Singleton


case class InvoiceConfig(startSubscribeDateField: String, endSubscribeDateField: String)

case class AppConfig(url: String,
                     realmId: String,
                     clientId: String,
                     clientSecret: String,
                     authId: String,
                     accessToken: String,
                     redirectUrl: String,
                    invoiceConfig: InvoiceConfig) {

}

@Singleton
class AppConfigFactory {

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JavaTimeModule)

  private[config] implicit val configLoader: ConfigLoader[AppConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)
    AppConfig(
      url = config.getString("url"),
      realmId = config.getString("realmId"),
      clientId = config.getString("clientId"),
      clientSecret = config.getString("clientSecret"),
      authId = config.getString("authId"),
      accessToken = config.getString("accessToken"),
      redirectUrl = config.getString("redirectUrl"),
      invoiceConfig = invoiceConfigLoader.load(config, "invoice")
    )
  }

  private[config] implicit val invoiceConfigLoader: ConfigLoader[InvoiceConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)

    InvoiceConfig(
      startSubscribeDateField = config.getString("startSubscribeDateField"),
      endSubscribeDateField = config.getString("endSubscribeDateField")
    )
  }


  private[config] lazy val config = ConfigFactory.load()

  private[config] lazy val oathConfig = new OAuth2ConfigBuilder(getIntuitConfig.clientId, getIntuitConfig.clientSecret)
    .callDiscoveryAPI(Environment.SANDBOX)
    .buildConfig()

  def getIntuitConfig = configLoader.load(config, "intuit")


  def getOathConfig = oathConfig

}
