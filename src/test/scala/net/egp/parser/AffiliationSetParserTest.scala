package net.egp.parser


import org.parboiled2._
import org.joda.time.DateTime
import org.scalatest.FunSuite

import scala.util.Try

class AffiliationSetParserTest extends FunSuite {

  def f = new {
    def parserRun(input: String) = {
      val parserTest = new AffiliationSetParser(ParserInput(input))
      val result = parserTest.document.run()
      val verboseFormatter = new ErrorFormatter(showTraces = true)
      if (result.isFailure) {
        result.failed.foreach {
          case error: ParseError => println(parserTest.formatError(error, verboseFormatter))
          case error => println(error)
        }
      }
      result
    }

    val affiliationSet =
      """
        |Affiliation Set Description:
      """.stripMargin

    val defaultAffiliation1 =
      """
        |Default Affiliation Name "default"
      """.stripMargin

    val nonDefaultAffiliation1 =
      """
        |,
        |Affiliation Name "nonDefault1"
      """.stripMargin
    val nonDefaultAffiliation2 =
      """
        |,
        |Affiliation Name "nonDefault2"
      """.stripMargin
    val nonDefaultAffiliation3 =
      """
        |,
        |Affiliation Name "nonDefault3"
      """.stripMargin

    val dateClause = "startDate 2016-01-01, endDate 2016-12-31"
    val medicalPlan = s"Medical Plan Year $dateClause"
  }

  //  test("positive date") { pending
  //    val result = f.parserRun("2016-05-14")
  //    assert(result.isSuccess)
  //    assert(result.get == new DateTime(2016,5,14,0,0))
  //  }

  test("negative date ") {
    pending
    val result = f.parserRun("1234-56-ab")
    assert(result.isFailure)
  }

  //  test("positive dateClause") { pending
  //    val result = f.parserRun(f.dateClause)
  //    assert(result.isSuccess)
  //    assert(result.get === DateClause(new DateTime(2016,1,1,0,0), new DateTime(2016,12,31,0,0)))
  //  }

  test("medical plan") {
    pending
    val result = f.parserRun(f.medicalPlan)
  }

  test("quoted string") {
    pending
    val result = f.parserRun("\"quotedstring\"")
    assert(result.isSuccess)
  }

  //  test("Default Affiliation") { pending
  //    val result = f.parserRun(f.defaultAffiliation1)
  //    assert(result.isSuccess)
  //    assert(result.get === Affiliation("default", true))
  //  }
  //
  //  test("Non Default Affiliation") {
  //    val result = f.parserRun(f.nonDefaultAffiliation)
  //    assert(result.isSuccess)
  //    assert(result.get === Affiliation("nonDefault", false))
  //  }

  //  test("NonDefaultAffiliationList") { pending
  //    val result = f.parserRun(
  //      f.nonDefaultAffiliation1
  //        + f.nonDefaultAffiliation2
  //        + f.nonDefaultAffiliation3
  //    )
  //    assert(result.isSuccess)
  //    assert(
  //      result.get === List(
  //        Affiliation("nonDefault1", false),
  //        Affiliation("nonDefault2", false),
  //        Affiliation("nonDefault3", false)
  //      ))
  //  }

  test("affiliationSet") {
    val result = f.parserRun(
      f.affiliationSet
        + f.medicalPlan
        + f.defaultAffiliation1
        + f.nonDefaultAffiliation1
        + f.nonDefaultAffiliation2
        + f.nonDefaultAffiliation3
    )
    assert(result.isSuccess)
    assert(
      result.get === AffiliationSet(
        MedicalPlanYear(DateClause(new DateTime(2016, 1, 1, 0, 0), new DateTime(2016, 12, 31, 0, 0))),
        List(
          Affiliation("default", true),
          Affiliation("nonDefault1", false),
          Affiliation("nonDefault2", false),
          Affiliation("nonDefault3", false)
        )
      )
    )
  }
}
