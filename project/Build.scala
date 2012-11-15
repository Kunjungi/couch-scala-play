import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "couchscalaplay"
    val appVersion      = "1.0-SNAPSHOT"
    val appDependencies = Seq(
    		"couchbase" % "couchbase-client" % "1.1-dp4"
    )
 
    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    		resolvers += "Couchbase Maven Repository" at "http://files.couchbase.com/maven2"
    )
    
    

}


