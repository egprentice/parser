package net.egp.parser

import org.parboiled2._
import org.scalatest.FunSuite

import scala.util.{Success, Try}

class CalculatorTest extends FunSuite {

  def f = new {
    val test1 = "1+1"
    val result1: Try[Int] = Success(2)

    val test2 = "24/8"
    val result2 = Success(3)

    val test3 = "2+f"

    def parserRun(s: String): Try[Int] = {
      val parserTest = new Calculator(ParserInput(s))
      val result = parserTest.InputLine.run()
      if (result.isFailure) {
        result.failed.foreach {
          case error: ParseError => println(parserTest.formatError(error))
          case error => println(error)
        }
      }
      result
    }
  }

  test(s"positive ${f.test1}") {
    assert(f.parserRun(f.test1) === f.result1)
  }

  test(s"positive ${f.test2}") {
    assert(f.parserRun(f.test2) === f.result2)
  }

  test(s"negative ${f.test3}") {
    val result = f.parserRun(f.test3)
    assert(result.isFailure)
  }
}

