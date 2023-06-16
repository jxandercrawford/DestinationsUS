package model

case class Airport(id: Int, name: String, city: String, state: String)

object Airport {

  def fromMap[A](m: Map[String, A]): Airport =
    Airport(
      m("id").toString.toInt,
      m("name").toString,
      m("city").toString,
      m("state").toString
    )

  def fromMapOption[A](m: Map[String, A]): Option[Airport] =
    try
      Some(fromMap(m))
    catch
      case _: Throwable => None
}
