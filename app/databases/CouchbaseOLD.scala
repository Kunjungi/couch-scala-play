package datasources;
 
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import collection.JavaConversions._ 
import collection.mutable.ArrayBuffer
import play._;
import com.couchbase.client.CouchbaseClient;
 
/**
* The `Couchbase` class acts a simple connection manager for the `CouchbaseClient`
* and makes sure that only one connection is alive throughout the application.
*
* You may want to extend and harden this implementation in a production environment.
*/
object CouchbaseOLD {
 
    /**
    * Holds the actual `CouchbaseClient`.
    */
     var client : CouchbaseClient = null;
 
    /**
    * Connects to Couchbase based on the configuration settings.
    *
    * If the database is not reachable, an error message is written and the
    * application exits.
    */
    def connect() : Boolean = {
        val hostname: String = Play.application().configuration().getString("couchbase.hostname");
        val port = Play.application().configuration().getString("couchbase.port");
        val bucket = Play.application().configuration().getString("couchbase.bucket");
        val password = Play.application().configuration().getString("couchbase.password");
 
         val uris = ArrayBuffer(URI.create("http://"+hostname+":"+port+"/pools"))
         client = new CouchbaseClient(uris, "default", "")
        
             client.set("HOLA", 0, "MONDO")
     println("Receiving -> " + client.get("hello"))
 
 
        try {
            client = new CouchbaseClient(uris, bucket, password);
        } catch {
          case e: IOException => {
            Logger.error("Error connection to Couchbase: " + e.getMessage());
            System.exit(0);
          }
        }
 
        true;
    }
 
    /**
    * Disconnect from Couchbase.
    */
    def  disconnect() : Boolean = {
        if(client == null) {
            return false;
        }
 
        return client.shutdown(3, TimeUnit.SECONDS);
    }
 
    /**
    * Returns the actual `CouchbaseClient` connection object.
    *
    * If no connection is established yet, it tries to connect. Note that
    * this is just in place for pure convenience, make sure to connect explicitly.
    */
    def  getConnection():CouchbaseClient = {
        if(client == null) {
            connect();
        }
 
        return client;
    }
 
}