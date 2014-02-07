package actors.ws

import play.api.libs.json._
import play.api.mvc.RequestHeader
import akka.actor._

object StatefullExampleWs extends WebSocketManager[StatefullExampleWs]  {
  case object AlertForSomething
  case class AlertOnlyMe(uuid: String)
}

class StatefullExampleWs extends StatefullWSManagerActor {
  
  def wsDevice = Props(WsDevice())
 
  case class WsDevice() extends Actor {
  
  import StatefullExampleWs._
  import WSClientMsgs._
  def receive = {
    manageBroadcast orElse {
    case x: JsFromClient =>
      (x.request.session.get("uuid")) match {
        case Some(uuid) =>
          context.become(withUuid(uuid, maxAnswares), true)
          self ! x
        case _ =>
      }
    }    
  }
  
  val maxAnswares = 3
  
  def withUuid(uuid: String, countdown: Int): Receive = {
    manageBroadcast orElse {
    case x: JsFromClient =>
      ((x.elem\"echo").asOpt[Boolean]) match {
        case Some(true) =>
          if (countdown>1) {
          context.parent ! JsToClient(Json.obj(
        		  				"answareToMe" -> true,
        		  				"uuid" -> uuid))        		  				
          context.become(withUuid(uuid,countdown-1), true)
          } else 
            context.parent ! JsToClient(Json.obj(
        		  				"answareToMe" -> true,
        		  				"boredToAnsware" -> true))
        case _ => 
          StatefullExampleWs.actor ! JsToClient(Json.obj("broadcast" -> "fromOneClient"))
      }
    }
  }
  
  def manageBroadcast: Receive = {
    case AlertForSomething =>
    	  context.parent ! JsToClient(Json.obj(
    		  "broadcast" -> "fromServer"
          ))
  }
  
  }
}