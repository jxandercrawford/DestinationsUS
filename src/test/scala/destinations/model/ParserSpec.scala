package destinations.model

import implicits.DateUtils
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.OptionValues.*

class ParserSpec extends AnyFlatSpec {

  // Parser variables
  val DELIMITER = """(?!\B"[^"]*),(?![^"]*"\B)"""
  val RAW_INPUT: String = """2020,1,2,1,6,2020-02-01,"MQ",20398,"MQ","N610AE","3380",10599,1059904,30599,"BHM","Birmingham, AL","AL","01","Alabama",51,13303,1330303,32467,"MIA","Miami, FL","FL","12","Florida",33,"0600","0558",-2.00,0.00,0.00,-1,"0600-0659",17.00,"0615","0851",12.00,"0902","0903",1.00,1.00,0.00,0,"0900-0959",0.00,"",0.00,122.00,125.00,96.00,1.00,661.00,3,,,,,,"",,,0,,,,,"",,,"",,,"","","",,,"",,,"","","",,,"",,,"","","",,,"",,,"","","",,,"",,,"","","""

  "extractFlight" should "return an Flight" in {
    val result = parser.parseFlight(RAW_INPUT)
    result shouldBe a [Flight]
  }

  "extractFlight" should "return with a origin Airport" in {
    val result = parser.parseFlight(RAW_INPUT)
    result shouldBe a [Flight]
    result.origin shouldBe a [Airport]
  }

  "extractFlight" should "return with an origin Airport with the correct id" in {
    val result = parser.parseFlight(RAW_INPUT).origin
    result.id shouldBe 10599
  }

  "extractFlight" should "return with an origin Airport with the correct name" in {
    val result = parser.parseFlight(RAW_INPUT).origin
    result.name shouldBe "BHM"
  }

  "extractFlight" should "return with an origin Airport with the correct city" in {
    val result = parser.parseFlight(RAW_INPUT).origin
    result.city shouldBe "Birmingham, AL"
  }

  "extractFlight" should "return with an origin Airport with the correct state" in {
    val result = parser.parseFlight(RAW_INPUT).origin
    result.state shouldBe "AL"
  }

  "extractFlight" should "return with a destination Airport" in {
    val result = parser.parseFlight(RAW_INPUT)
    result shouldBe a [Flight]
    result.destination shouldBe a [Airport]
  }

  "extractFlight" should "return with an destination Airport with the correct id" in {
    val result = parser.parseFlight(RAW_INPUT).destination
    result.id shouldBe 13303
  }

  "extractFlight" should "return with an destination Airport with the correct name" in {
    val result = parser.parseFlight(RAW_INPUT).destination
    result.name shouldBe "MIA"
  }

  "extractFlight" should "return with an destination Airport with the correct city" in {
    val result = parser.parseFlight(RAW_INPUT).destination
    result.city shouldBe "Miami, FL"
  }

  "extractFlight" should "return with an destination Airport with the correct state" in {
    val result = parser.parseFlight(RAW_INPUT).destination
    result.state shouldBe "FL"
  }

  "extractFlight" should "return with the correct date" in {
    val result = parser.parseFlight(RAW_INPUT)
    result.date shouldBe "2020-02-01".toDate("yyyy-MM-dd")
  }

  "extractFlight" should "return with the correct distance" in {
    val result = parser.parseFlight(RAW_INPUT)
    result.distance shouldBe 661.00
  }

  "extractFlight" should "return with the correct airtime" in {
    val result = parser.parseFlight(RAW_INPUT)
    result.airtime shouldBe 96
  }

  "extractFlight" should "return with the correct departDelay" in {
    val result = parser.parseFlight(RAW_INPUT)
    result.departDelay shouldBe -2
  }

  "extractFlight" should "return with the correct taxiOut" in {
    val result = parser.parseFlight(RAW_INPUT)
    result.taxiOut shouldBe 17
  }

  "extractFlight" should "return with the correct arriveDelay" in {
    val result = parser.parseFlight(RAW_INPUT)
    result.arriveDelay shouldBe 1
  }

  "extractFlight" should "return with the correct taxiIn" in {
    val result = parser.parseFlight(RAW_INPUT)
    result.taxiIn shouldBe 12
  }

  "extractFlightOption" should "return an Option[Flight]" in {
    val result = parser.parseFlightOption(RAW_INPUT)
    result shouldBe a [Option[Flight]]
  }
}
