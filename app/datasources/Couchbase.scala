package datasources;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import play._;
import com.couchbase.client.CouchbaseClient;

import collection.JavaConversions._
import collection.mutable.ArrayBuffer

/**
 * Creates and manages connections to the Couchbase cluster
 * based on the given configuration settings.
 * As all members in the original java class are static we use directly a companion object.
 */
object Couchbase {

  /**
   * The instance of the client to connect to.
   */
  var client: CouchbaseClient = null;

  /**
   * Imported play configuration.
   */
  val config: Configuration = Play.application().configuration();

  /**
   * Connect to a Couchbase cluster.
   */
  def connect(): Boolean = {
    val hostname: String = config.getString("couchbase.hostname");
    val port = config.getString("couchbase.port");
    val bucket = config.getString("couchbase.bucket");
    val password = config.getString("couchbase.password");
    val uris = ArrayBuffer(URI.create("http://" + hostname + ":" + port + "/pools"))

    synchronized {
      client = new CouchbaseClient(uris, "default", "")

      try {
        client = new CouchbaseClient(uris, bucket, password);
      } catch {
        case e: IOException => {
          Logger.error("Error connection to Couchbase: " + e.getMessage());
          System.exit(0);
        }
      }
    }
    return true;
  }

  /**
   * Disconnect from a Couchbase cluster.
   */
  def disconnect(): Boolean = {
    synchronized {
      if (client == null) {
        return false;
      }
      val timeout = config.getLong("couchbase.shutdownTimeout");
      return client.shutdown(timeout, TimeUnit.SECONDS);
    }
  }

  /**
   * Return the client object in a safe way. If the connection was
   * not opened previously, go ahead and create it.
   */
  def getInstance(): CouchbaseClient = {
    if (client == null) {
      synchronized {
        if (client == null) {
          connect();
        }
      }
    }

    return client;
  }

}