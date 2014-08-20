package controllers

import com.google.inject.Inject
import org.joda.time.format.ISODateTimeFormat
import play.api.mvc.{Action, Controller}

final class HealthCheck @Inject()() extends Controller {

  def respond = Action { implicit request =>
    val now = org.joda.time.DateTime.now()
    val isoDateTimeString = s"${ISODateTimeFormat.yearMonthDay().print(now)} ${ISODateTimeFormat.hourMinute().print(now)}"

    Ok(views.html.healthCheck(isoDateTimeString))
  }
}
