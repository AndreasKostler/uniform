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

package au.com.cba.omnia.uniform.dependency

import sbt._, Keys._

object UniformDependencyPlugin extends Plugin {
  def uniformDependencySettings: Seq[Sett] = Seq[Sett](
    resolvers ++= Seq(
      "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
    , "releases" at "http://oss.sonatype.org/content/repositories/releases"
    , "Concurrent Maven Repo" at "http://conjars.org/repo"
    , "Clojars Repository" at "http://clojars.org/repo"
    , "Twitter Maven" at "http://maven.twttr.com"
    , "Hadoop Releases" at "https://repository.cloudera.com/content/repositories/releases/"
    , "cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos/"
    , "commbank-releases" at "http://commbank.artifactoryonline.com/commbank/ext-releases-local"
    , "commbank-releases-private" at "https://commbank.artifactoryonline.com/commbank/libs-releases-local"
    , "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
    )
  )

  val strictDependencySettings: Seq[Sett] = Seq[Sett](
    conflictManager := ConflictManager.strict,
    dependencyOverrides <+= scalaVersion(sv => "org.scala-lang" % "scala-library" % sv)
  ) ++ hadoopCP.modules.map(module =>
    dependencyOverrides in Test += module
  )

  def noHadoop(module: ModuleID) = module.copy(exclusions = module.exclusions ++ hadoopCP.exclusions)

  object hadoopCP {
    val modules = List[ModuleID](
      "org.slf4j"                 % "slf4j-api"          % "1.7.5",
      "org.slf4j"                 % "slf4j-log4j12"      % "1.7.5",
      "log4j"                     % "log4j"              % "1.2.17",
      "commons-logging"           % "commons-logging"    % "1.1.3",
      "commons-codec"             % "commons-codec"      % "1.5",
      "commons-lang"              % "commons-lang"       % "2.6",
      "commons-httpclient"        % "commons-httpclient" % "3.1",
      "org.apache.httpcomponents" % "httpclient"         % "4.2.5",
      "org.apache.httpcomponents" % "httpcore"           % "4.2.5",
      "com.google.guava"          % "guava"              % "11.0.2",
      "org.codehaus.jackson"      % "jackson-mapper-asl" % "1.8.8",
      "org.codehaus.jackson"      % "jackson-core-asl"   % "1.8.8",
      "org.xerial.snappy"         % "snappy-java"        % "1.0.4.1",
      "com.google.protobuf"       % "protobuf-java"      % "2.5.0",
      "io.netty"                  % "netty"              % "3.6.2.Final",
      "junit"                     % "junit"              % "4.11",
      "jline"                     % "jline"              % "0.9.94",

      // asm changed from asm.asm-3.2, to org.ow2.asm.asm-4.0, so can't naively pin the version
      // TODO consider doing something more complicated to change all org.ow2.asm.asm deps to asm.asm-3.2
      // for now just ensure that all org.ow2.asm.asm jars are as close to 3.2 as possible
      "org.ow2.asm"               % "asm"                % "4.0",
      "asm"                       % "asm"                % "3.2",

      // as far as I can tell, libthrift is not on the hadoop classpath
      // different versions of libthrift are pulled in by depend.hive, depend.scrooge, and parquet-cascading
      // different projects could rely on different combinations of these, so cannot rely on "canonical" source
      // TODO parquet-cascading is marked as provided, so am I sure this is not on hadoop classpath? do I need to add extra jars to depend.parquet?
      // TODO consider replacing pinned version with logic which chooses the latest version from the versions pulled in, just for this jar
      // for now using dependencyOverrides to pin version to latest one we could possibly pull in
      "org.apache.thrift"         % "libthrift"          % "0.9.0-cdh5-2"
    )

    val dependencies = modules.map(m => m % "provided" intransitive)
    val exclusions   = modules.map(m => ExclusionRule(m.organization, m.name))
  }

  object depend {
    object versions {
      def hadoop       = "2.5.0-mr1-cdh5.2.0"
      def scalaz       = "7.1.0"
      def scalazStream = "0.5a"      // Needs to align with what is required by specs2
      def specs        = "2.4.13"
      def scalacheck   = "1.11.4"    // Needs to align with what is required by scalaz-scalacheck-binding and specs2
      def shapeless    = "2.0.0-M1"  // Needs to align with what is required by specs2
      def mockito      = "1.9.0"
      def jodaTime     = "2.3"
      def nscalaTime   = "1.2.0"
      def scalding     = "0.12.0"
      def algebird     = "0.7.1"
      def log4j        = "1.2.17"
      def slf4j        = "1.7.5"
      def scallop      = "0.9.5"
      def pegdown      = "1.4.2"
      def classutil    = "1.0.5"
      def scrooge      = "3.14.1"
      def bijection    = "0.6.3"
      def hive         = "0.13.1-cdh5.2.0"
      def parquet      = "1.5.0-cdh5.2.0"
    }

    def omnia(project: String, version: String): Seq[ModuleID] =
      Seq("au.com.cba.omnia" %% project % version)

    def scaldingproject(
      hadoop: String     = versions.hadoop,
      scalding: String   = versions.scalding,
      algebird: String   = versions.algebird,
      log4j: String      = versions.log4j,
      slf4j: String      = versions.slf4j,
      specs: String      = versions.specs,
      mockito: String    = versions.mockito,
      scalacheck: String = versions.scalacheck,
      scalaz: String     = versions.scalaz,
      pegdown: String    = versions.pegdown,
      classutil: String  = versions.classutil
    ) =
      this.hadoop(hadoop) ++
      this.scalding(scalding, algebird) ++
      this.logging(log4j, slf4j) ++
      this.testing(specs, mockito, scalacheck, scalaz, pegdown, classutil)

    def hadoopClasspath = hadoopCP.dependencies

    def hadoop(version: String = versions.hadoop) = Seq(
      "org.apache.hadoop"        %  "hadoop-client"                 % version        % "provided",
      "org.apache.hadoop"        %  "hadoop-core"                   % version        % "provided"
    ) map (noHadoop(_))

    def hive(version: String = versions.hive) = Seq(
      "org.apache.hive"          % "hive-exec"                      % version
    ) map (noHadoop(_))

    def scalaz(version: String = versions.scalaz) = Seq(
      "org.scalaz"               %% "scalaz-core"                   % version,
      "org.scalaz"               %% "scalaz-concurrent"             % version
    )

    def scalazStream(version: String = versions.scalazStream) = Seq(
      "org.scalaz.stream"        %% "scalaz-stream"                 % version
    )

    def shapeless(version: String = versions.shapeless) = Seq(
      "com.chuusai"              % "shapeless_2.10.3"               % version
    )

    def testing(
      specs: String = versions.specs, mockito: String = versions.mockito,
      scalacheck: String = versions.scalacheck, scalaz: String = versions.scalaz,
      pegdown: String = versions.pegdown, classutil: String = versions.classutil
    ) = Seq(
      "org.specs2"               %% "specs2"                        % specs       % "test" exclude("org.scalacheck", "scalacheck_2.10"),
      "org.mockito"              %  "mockito-all"                   % mockito     % "test",
      "org.scalacheck"           %% "scalacheck"                    % scalacheck  % "test",
      "org.scalaz"               %% "scalaz-scalacheck-binding"     % scalaz      % "test"
    )

    def time(joda: String = versions.jodaTime, nscala: String = versions.nscalaTime) = Seq(
      "joda-time"                %  "joda-time"                     % joda,
      "com.github.nscala-time"   %% "nscala-time"                   % nscala
    )

    def scalding(scalding: String = versions.scalding, algebird: String = versions.algebird) = Seq(
      noHadoop("com.twitter"     %% "scalding-core"                 % scalding),
      "com.twitter"              %% "algebird-core"                 % algebird
    )

    def logging(log4j: String = versions.log4j, slf4j: String = versions.slf4j) = Seq(
      "log4j"                    %  "log4j"                         % log4j       % "provided",
      "org.slf4j"                %  "slf4j-api"                     % slf4j       % "provided",
      "org.slf4j"                %  "slf4j-log4j12"                 % slf4j       % "provided"
    )

    def scallop(version: String = versions.scallop) = Seq(
      "org.rogach"               %% "scallop"                       % version
    )

    def scrooge(scrooge: String = versions.scrooge, bijection: String = versions.bijection) = Seq(
      "com.twitter"              %% "scrooge-core"                  % scrooge,
      "com.twitter"              %% "bijection-scrooge"             % bijection exclude("com.twitter", "scrooge-core_2.10")
    )

    def parquet(version: String = versions.parquet) = Seq(
      "com.twitter"              % "parquet-cascading"              % version     % "provided"
    ) map (noHadoop(_))
  }
}
