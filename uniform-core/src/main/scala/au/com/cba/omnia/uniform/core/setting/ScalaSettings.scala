//   Copyright 2014 Commonwealth Bank of Australia
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package au.com.cba.omnia.uniform.core
package setting

import sbt._
import Keys._

object ScalaSettings extends Plugin {
  object scala {
    def settings = Seq(
      scalaVersion := "2.10.5",
      crossScalaVersions := Seq("2.10.5"),
      scalacOptions ++= Seq(
        "-deprecation",
        "-unchecked",
        "-Ywarn-all",
        "-Xlint",
        "-feature",
        "-language:_",
        "-target:jvm-1.7"
      ),
      javacOptions ++= Seq(
        "-Xlint:unchecked",
        "-source", "1.7",
        "-target", "1.7"
      )
    )
  }
}
