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
package version

import sbt._, Keys._

import java.util.Date
import java.text.SimpleDateFormat

object VersionInfoPlugin extends Plugin {
  import Times._
  import GitInfo._

  lazy val rootPackage = SettingKey[String]("root-package")

  lazy val versionInfoSettings: Seq[Sett] = Seq[Sett](
    (sourceGenerators in Compile) <+= (sourceManaged in Compile, target, name, version, baseDirectory, rootPackage).map((src, target, name, version, base, pkg) => {
      val scala = src / "info.scala"
      val txt = target / "VERSION.txt"
      IO.write(scala, """package %s
                       |object VersionInfo {
                       |  val version = "%s"
                       |  val git = "%s"
                       |  val date = "%s"
                       |}""".stripMargin.format(pkg, version, commit(base), timestamp(now)))
      IO.write(txt, """VERSION=%s
                       |GIT=%s
                       |DATE=%s""".stripMargin.format(version, commit(base), timestamp(now)))
      Seq(scala)
    })
  )
}
