package net.egp.parser

import org.parboiled2._
import org.joda.time.DateTime
import shapeless.{::, HNil}

case class MedicalPlanYear(startEnd: DateClause)
case class DateClause(start: DateTime, end: DateTime)
case class Affiliation(name: String, default: Boolean=false) {
  override def toString: String = s"Affiliation($name, $default)"
}
object Affiliation{
   def applyS(s: String, default: Boolean): Affiliation =
    new Affiliation(s.replaceAll("\"",""), default)
}
case class AffiliationSet(MedicalPlan: MedicalPlanYear, affiliations: List[Affiliation])

class AffiliationSetParser(val input: ParserInput) extends Parser {

  implicit def whiteSpaceString(s: String): Rule0 = rule {
    str(s) ~ zeroOrMore(WSCHAR)
  }

  def document = rule {
    zeroOrMore(WSCHAR) ~ affiliationSet ~ EOI
  }

  def whiteSpace = rule {
    oneOrMore(WSCHAR)
  }

  val WSCHAR = CharPredicate(" \t\n\r")

  def word = rule {
    oneOrMore(CharPredicate.Alpha)
  }

  def date = rule {
    YearDigits ~ "-" ~ MonthDigits ~ "-" ~ DayDigits ~> { (y: Int, m: Int, d: Int) => new DateTime(y, m, d, 0, 0) }
  }

  def YearDigits: Rule[HNil, ::[Int, HNil]] = rule {
    capture(4.times(CharPredicate.Digit)) ~>
      ((y: String) => y.toInt)
  }

  def MonthDigits: Rule[HNil, ::[Int, HNil]] = rule {
    capture("0" ~ CharPredicate.Digit | "1" ~ anyOf("012")) ~>
      ((m: String) => m.toInt)
  }

  def DayDigits: Rule[HNil, ::[Int, HNil]] = rule {
    capture(anyOf("012") ~ CharPredicate.Digit | "3" ~ anyOf("01")) ~>
      ((d: String) => d.toInt)
  }

  def startDate = rule {
    atomic("startDate") ~ date ~> ((d: DateTime) => d)
  }

  def endDate = rule {
    atomic("endDate") ~ date ~> ((d: DateTime) => d)
  }

  def dateClause = rule {
    startDate ~ ',' ~ oneOrMore(WSCHAR) ~ endDate ~>
      ((start: DateTime, end: DateTime) => DateClause(start, end))
  }

  def affiliationSet = rule {
    atomic("Affiliation Set Description:") ~ medicalPlan ~ whiteSpace ~
      defaultAffiliation ~
      nonDefaultAffiliationList ~>
      ((m,d,n) =>  AffiliationSet(m, d :: n))
  }

  def medicalPlan = rule {
    atomic("Medical Plan Year") ~ dateClause ~> ((date: DateClause) => MedicalPlanYear(date))
  }

  def defaultAffiliation: Rule[HNil, ::[Affiliation, HNil]] = rule {
    atomic("Default Affiliation Name") ~ capture(quotedString) ~ whiteSpace ~>
      ((name: String) => Affiliation.applyS(name, true))
  }

  def nonDefaultffiliation: Rule[HNil, ::[Affiliation, HNil]] = rule {
    "," ~ atomic("Affiliation Name") ~ capture(quotedString) ~ whiteSpace ~>
      ((name: String) => Affiliation.applyS(name, false))
  }

  def nonDefaultAffiliationList = rule {
    zeroOrMore(nonDefaultffiliation) ~> ((as) => as.toList)
  }

  def quotedString = rule {
    CharPredicate("\"") ~ oneOrMore(CharPredicate.AlphaNum) ~ CharPredicate("\"")
  }
}
