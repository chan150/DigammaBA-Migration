package kr.acon.parser

class TrillionBAParser extends Parser {
  // BA model (TrillionBA)
  var ban = 1000000l
  var bam = 2
  var bam0 = 2
  var bal = 100000l

  // BA model advanced
  var bap = 0d
  var baStatic = false
  var model = "none"

  val termTrillionBA = List("-ba.n", "-ba.m", "-ba.m0", "-ba.l")
  val termTrillionBAadv = List("-ba.model", "-ba.p", "-ba.static")

  override val term = termBasic.++(termTrillionBA).++(termTrillionBAadv)

  override def setParameters(name: String, value: String) {
    super.setParameters(name, value)
    name match {
      case "ba.n" => ban = value.toLong
      case "ba.m" => bam = value.toInt
      case "ba.m0" => bam0 = value.toInt
      case "ba.l" => bal = value.toInt
      case "ba.p" => bap = value.toDouble
      case "ba.static" => baStatic = value.toBoolean
      case "ba.model" => {
        value match {
          case "a" | "b" => model = value
          case _ => model = "none"
        }
      }
      case _ => // important to set
    }
  }
}
