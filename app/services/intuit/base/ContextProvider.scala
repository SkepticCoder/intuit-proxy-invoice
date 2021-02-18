package services.intuit.base

import com.intuit.ipp.core.{Context, ServiceType}
import com.intuit.ipp.security.OAuth2Authorizer
import config.AppConfigFactory

import javax.inject.{Inject, Singleton}

@Singleton
class ContextProvider @Inject() (configFactory: AppConfigFactory) {

  def getContext(accessTokenProvider: () => String) = {
    val appConfig = configFactory.getIntuitConfig
    val oauth = new OAuth2Authorizer(accessTokenProvider())
    new Context(oauth, ServiceType.QBO, appConfig.realmId)
  }
}
