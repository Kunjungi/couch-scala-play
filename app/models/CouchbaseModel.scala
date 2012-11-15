package models;

import com.couchbase.client.CouchbaseClient;
import datasources.Couchbase;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.spy.memcached.internal.OperationFuture;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Base Model to provide most of the operations needed for all other
 * models.
 *
 * Generates a model with the given key.
 * @param key The key of the model. If it is null or empty, a unique one will
 *    be created automatically.
 *
 */
class CouchbaseModel(var pkey: String) {
  /**
   * Holds the unique key inside the bucket.
   */
  @JsonIgnore
  var key: String = null;
  
  if (pkey == null || pkey.isEmpty()) {
    pkey = CouchbaseModel.generateKey();
  }

  this.key = pkey;

  /**
   * The connection to the cluster.
   */
  var connection: CouchbaseClient = Couchbase.getInstance();



  //var value: String = null;

  /**
   * Create a new model and generate a unique key.
   */
  def this() = {
    this(null);
  }

  /**
   * Get the key for the document.
   * @return
   */
  def getKey(): String = {
    return this.key;
  }

  /**
   * Set the key for the document. A key is required.
   * @param key
   */
  def setKey(key: String): CouchbaseModel = {
    this.key = key;
    return this;
  }

  /**
   * Shortcut method to save and dont wait for the reply.
   * @return
   */
  def save(): Boolean = {
    return save(false);
  }

  /**
   * Stores the JSONified object in Couchbase.
   *
   * @return whether the save was a success or not.
   */
  def save(wait: Boolean): Boolean = {
    var setFuture: OperationFuture[java.lang.Boolean] = connection.set(getKey(), 0, this.toJson());
    if (wait == false) {
      return true;
    }

    var success: Boolean = true;
    try {
      success = setFuture.get();
    } catch {
      case ex: Exception => throw new RuntimeException("Failed while waiting for the operation: " + ex);
    }
    return success;
  }

    /**
   * Stores the JSONified object in Couchbase.
   *
   * @return whether the save was a success or not.
   */
  def save(json:String): Boolean = {
		  var setFuture: OperationFuture[java.lang.Boolean] = connection.set(getKey(), 0, json);
    return true;

  }

  
  // Recommandation of transforming a static method is using a companion object (singleton).
  object CouchbaseModel {
    /**
     * If no key is set, it will generate a UUID id.
     * @return
     */
    def generateKey(): String = {
      val className = getClass().getSimpleName().toLowerCase();
      val uuid = UUID.randomUUID();
      return className + "-" + uuid.toString();
    }
  }

  /**
   * Converts the current object to JSON
   * @return
   */
  def toJson(): String = {
    val mapper: ObjectMapper = new ObjectMapper();
    var result: String = "";

    try {
      result = mapper.writeValueAsString(this);
    } catch {
      case ex: IOException => throw new RuntimeException("Could not convert to JSON: " + ex);
    }

    return result;
  }

}
