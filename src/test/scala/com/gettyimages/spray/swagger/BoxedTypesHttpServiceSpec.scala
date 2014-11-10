package com.gettyimages.spray.swagger

import com.wordnik.swagger.core.SwaggerContext
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import spray.testkit._
import scala.reflect.runtime.universe._
import akka.actor.ActorSystem
import spray.http._
import org.json4s.jackson.JsonMethods._
import org.json4s._
import com.wordnik.swagger.model.ResponseMessage

class BoxedTypesHttpServiceSpec
  extends WordSpec
  with ShouldMatchers
  with ScalatestRouteTest {

  val swaggerService = new SwaggerHttpService {
    override def apiTypes = Seq(typeOf[BoxedTypesHttpService], typeOf[BoxedTypesWithDataTypeHttpService])
    override def apiVersion = "1.0"
    override def baseUrl = "http://some.domain.com"
    override def docsPath = "api-doc"
    override def actorRefFactory = ActorSystem("swagger-spray-test")
  }

  implicit val formats = org.json4s.DefaultFormats

  def printPretty(jValue: JValue): Unit ={
    import org.json4s.jackson.JsonMethods._
    val str = pretty(jValue)
    println(str)
  }

  "The BoxedTypesHttpService" when {
    "accessing the root doc path" should {
      "return the basic set of api info" in {
        Get("/api-doc") ~> swaggerService.routes ~> check {
          handled shouldBe true
          contentType shouldBe ContentTypes.`application/json`
          val response = parse(responseAs[String])
          (response \ "apiVersion").extract[String] shouldEqual "1.0"
          (response \ "swaggerVersion").extract[String] shouldEqual "1.2"
          val apis = (response \ "apis").extract[Array[JValue]]
          apis.size shouldEqual 2
          val api = apis.filter(a => (a \ "path").extract[String] == "/box").head
          (api \ "description").extract[String] shouldEqual "This is the BoxedTypes resource"
          (api \ "path").extract[String] shouldEqual "/box"
        }
      }
    }
    "accessing a resource" should {
      "return the api description for a resource with boxed types" in {
        Get("/api-doc/box") ~> swaggerService.routes ~> check {
          handled shouldBe true
          val responseStr = responseAs[String]
          val response = parse(responseStr)
          printPretty(response)
          (response \ "resourcePath").extract[String] shouldEqual "/box"
          val apis = (response \ "apis").extract[Array[JValue]]
          apis.size shouldEqual 1
          val api = apis.filter(a => (a \ "path").extract[String] == "/box").head
          (api \ "path").extract[String] shouldEqual "/box"
          val ops = (api \ "operations").extract[Array[JValue]]
          ops.size shouldEqual 1
          val models = (response \ "models").extract[JObject]
          val box = (models \ "BoxedTypesIssue31").extract[JObject]
          println("BoxedTypesIssue31 Model = " + box.toString)
          (box \ "id").extract[String] shouldEqual "BoxedTypesIssue31"
          (box \ "properties" \ "stringSeq" \ "type").extract[String] shouldEqual "array"
          (box \ "properties" \ "stringSeq" \ "items" \ "type").extract[String] shouldEqual "string"
          (box \ "properties" \ "intSeq" \ "type").extract[String] shouldEqual "array"
          val intSeqType: JValue = (box \ "properties" \ "intSeq" \ "items" \ "type")
          // TODO - These commented out lines should pass when Issue#31 is resolved
          // intSeqType should not be (JNothing)
          // intSeqType.extract[String] shouldEqual "integer"
          (box \ "properties" \ "stringOpt" \ "type").extract[String] shouldEqual "string"
          val intOptType: JValue = (box \ "properties" \ "intOpt" \ "type")
          // intOptType should not be (JNothing)
          // intOptType.extract[String] shouldEqual "integer"
        }
      }
      "return the api description for a boxed type resource using dataType" in {
        Get("/api-doc/boxWithDataType") ~> swaggerService.routes ~> check {
          handled shouldBe true
          val responseStr = responseAs[String]
          val response = parse(responseStr)
          printPretty(response)
          (response \ "resourcePath").extract[String] shouldEqual "/boxWithDataType"
          val apis = (response \ "apis").extract[Array[JValue]]
          apis.size shouldEqual 1
          val api = apis.filter(a => (a \ "path").extract[String] == "/boxWithDataType").head
          (api \ "path").extract[String] shouldEqual "/boxWithDataType"
          val ops = (api \ "operations").extract[Array[JValue]]
          ops.size shouldEqual 1
          val models = (response \ "models").extract[JObject]
          val box = (models \ "BoxedTypesIssue31WithDataType").extract[JObject]
          println("BoxedTypesIssue31WithDataType Model = " + box.toString)
          (box \ "id").extract[String] shouldEqual "BoxedTypesIssue31WithDataType"
          (box \ "properties" \ "stringSeq" \ "type").extract[String] shouldEqual "array"
          (box \ "properties" \ "stringSeq" \ "items" \ "type").extract[String] shouldEqual "string"
          (box \ "properties" \ "intSeq" \ "type").extract[String] shouldEqual "array"
          val intSeqType: JValue = (box \ "properties" \ "intSeq" \ "items" \ "type")
          intSeqType should not be (JNothing)
          // TODO - w/o swagger-core change, the data type will be int
          intSeqType.extract[String] shouldEqual "integer"
          (box \ "properties" \ "stringOpt" \ "type").extract[String] shouldEqual "string"
          val intOptType: JValue = (box \ "properties" \ "intOpt" \ "type")
          intOptType should not be (JNothing)
          intOptType.extract[String] shouldEqual "integer"
        }
      }
    }
  }
}