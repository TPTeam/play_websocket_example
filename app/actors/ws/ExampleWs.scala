package actors.ws

import play.api.libs.iteratee._
import play.api.libs.json._
import play.api.mvc.RequestHeader

object ExampleWs extends WebSocketManager[ExampleWs]  {
  case object AlertForSomething
  case class AlertOnlyMe(uuid: String)
}

class ExampleWs extends WSManagerActor {
  
  import ExampleWs._
  import WSClientMsgs._
 
  override def operative(implicit request: RequestHeader) = {
    (wsClient) => { 
    case AlertOnlyMe(uuid) =>
      for {
        clientUUID <- request.session.get("uuid")
        if (clientUUID.compareTo(uuid) == 0)
      } {
        wsClient ! JsToClient(Json.obj(
    		  "answareToMe" -> true
    	    ))
      }
    case AlertForSomething =>
    	  wsClient ! JsToClient(Json.obj(
    		  "broadcast" -> "fromServer"
          ))
    case JsFromClient(something) =>
      ((something\"echo").asOpt[Boolean]) match {
        case Some(true) =>
          wsClient ! AlertOnlyMe(request.session.get("uuid").getOrElse(""))
        case _ => 
          self ! JsToClient(Json.obj("broadcast" -> "fromOneClient"))
      }
  }
  }
  
}