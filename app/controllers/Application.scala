package controllers

import play.api._

import play.api.mvc._
import models.CouchbaseModel
import com.couchbase.client.CouchbaseClient;

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def setDocument(key: String, value: String) = Action {
    var cm: CouchbaseModel = new CouchbaseModel(key);
    cm.save(value);
    Ok(views.html.index("You have saved the following key-value pair." + key + " => " + value))
  }

}