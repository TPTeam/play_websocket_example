package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Application extends Controller {
  
  def index = Action {implicit request =>
    Ok(views.html.index("Choose one"))
  }
  
  def indexStateless = Action {implicit request =>
    Ok(views.html.indexStateless("Welcome! Let's Play with your brand new stateless websocket!")).withSession(
        ("uuid" -> java.util.UUID.randomUUID.toString)
      )
  }
  def indexStatefull = Action {implicit request =>
    Ok(views.html.indexStatefull("Welcome! Let's Play with your brand new statefull websocket!")).withSession(
        ("uuid" -> java.util.UUID.randomUUID.toString)
      )
  }
  
  def ws = 
    WebSocket.async[JsValue] {implicit request => 
	  actors.ws.ExampleWs.control
  }
  
  def statefullWs = 
    WebSocket.async[JsValue] {implicit request => 
	  actors.ws.StatefullExampleWs.control
  }
  
  def testBroadCast = Action {
    import actors.ws.ExampleWs
    import actors.ws.ExampleWs._
    ExampleWs.actor ! AlertForSomething
    Ok("Msg2 Sent")
  }
  
  def testBroadCastStatefull = Action {
    import actors.ws.StatefullExampleWs
    import actors.ws.StatefullExampleWs._
    StatefullExampleWs.actor ! AlertForSomething
    Ok("Msg2 Sent to statefull")
  }
}