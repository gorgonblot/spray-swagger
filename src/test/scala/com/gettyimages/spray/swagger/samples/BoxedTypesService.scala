/**
 * Copyright 2013 Getty Imges, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gettyimages.spray.swagger

import org.json4s.jackson.Serialization
import spray.routing.{Route, HttpService}
import com.wordnik.swagger.annotations._
import spray.httpx.Json4sSupport
import scala.annotation.meta.field
import org.json4s.jackson.Serialization
import org.json4s.{Formats, NoTypeHints}
import javax.ws.rs.Path

// Issue #31: https://github.com/gettyimages/spray-swagger/issues/31
@ApiModel(description = "Options of boxed types produces an Object ref instead of correct type")
case class BoxedTypesIssue31WithDataType(stringSeq: Seq[String], stringOpt: Option[String],
   @(ApiModelProperty @field)(value = "Integers in a Sequence Box", dataType = "List[int]") intSeq: Seq[Int],
   @(ApiModelProperty @field)(value = "Integer in an Option Box", dataType = "int") intOpt: Option[Int],
                              justInt: Int)

@ApiModel(description = "Options of boxed types produces an Object ref instead of correct type")
case class BoxedTypesIssue31(stringSeq: Seq[String], stringOpt: Option[String],
                             @(ApiModelProperty @field)(value = "Integers in a Sequence Box") intSeq: Seq[Int],
                             @(ApiModelProperty @field)(value = "Integer in an Option Box") intOpt: Option[Int],
                             justInt: Int)

@Api(value="/box", description="This is the BoxedTypes resource")
trait BoxedTypesHttpService extends HttpService with Json4sSupport {

  implicit def formats: Formats = Serialization.formats(NoTypeHints)

  @ApiOperation(value="List all of the BoxedTypes objects",
    notes = "",
    response = classOf[BoxedTypesIssue31],
    httpMethod = "GET",
    nickname = "getBoxedTypesIssue31"
  )
  @ApiResponse(code = 0, message = "profile_2")
  def getBoxedTypesIssue31 = path("box"){
    complete("BoxedTypesIssue31")
  }

  val routes: Route = get {getBoxedTypesIssue31}
}

@Api(value="/boxWithDataType", description="This is the BoxedTypes resource with DataType annotations")
trait BoxedTypesWithDataTypeHttpService extends HttpService with Json4sSupport {

  implicit def formats: Formats = Serialization.formats(NoTypeHints)


  @ApiOperation(value="List all of the BoxedTypes with Datatype property objects",
    notes = "",
    response = classOf[BoxedTypesIssue31WithDataType],
    httpMethod = "GET",
    nickname = "getBoxedTypesIssue31WithDataType"
  )
  @ApiResponse(code = 0, message = "profile_2")
  def getBoxedTypesIssue31WithDataType = path("boxWithDataType"){
    complete("BoxedTypesIssue31WithDataType")
  }

  val route: Route = get {getBoxedTypesIssue31WithDataType}
}
