import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "websocket_example"
  val appVersion      = "1.1"

  val appDependencies = Seq(
    "websocket_plugin" % "websocket_plugin_2.10" % "0.4.0"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(

      resolvers += Resolver.url("TPTeam Repository", url("http://tpteam.github.io/snapshots/"))(Resolver.ivyStylePatterns)

  )

}
