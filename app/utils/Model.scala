package utils

import org.joda.time.DateTime

case class TimerEventPost(path: String, timerEvent: TimerEvent)
case class TimerEvent(duration: Int, dateTime: DateTime = DateTime.now())
case class TimerEventRequest(path: String)
