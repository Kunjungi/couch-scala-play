
import play.api._
 
import java.net.URI;
import java.net.ConnectException;

import datasources.Couchbase;

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
    Couchbase.connect();
  }  
  
  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    Couchbase.disconnect();
  }  
    
}