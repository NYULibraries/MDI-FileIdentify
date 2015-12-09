package mdi.test

object TestProtocol {
  case object GetHeartbeat
  case object GetChannel
  case object Dump

  case class TestRequest(message: String)
  case class TestResponse(message: String)
  case class IdRequest(uuid: String)
}
