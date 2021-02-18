package services.intuit.base

import com.intuit.ipp.core.Context


sealed trait ResultAuthorize

final case class RedirectAuthorize(url: String) extends ResultAuthorize

final case class SuccessAuthorize(context: Context) extends ResultAuthorize

final case class NonAnothorize() extends ResultAuthorize