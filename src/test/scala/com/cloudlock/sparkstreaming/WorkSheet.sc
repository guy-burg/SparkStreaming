import org.json4s._
import org.json4s.jackson.JsonMethods._


def jsonStrToMap(jsonStr: String): Map[String, Any] = {
  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

  parse(jsonStr).extract[Map[String, Any]]
}

val j = """{"Name":"abc", "age":10}"""


jsonStrToMap(j)("Name").toString