package controllers

import akka.Inquire._
import domain._
import domain.json._
import javax.inject.Inject
import play.api.libs.json._
import services.Services
import shapeless.TypeCase
import store.{RootActors, Stores}

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

class UserController @Inject()(
  services : Services,
  actors   : RootActors,
  stores   : Stores) extends ControllerSupport (services) {

  val SuccessToken = TypeCase[Success[Token]]

  def create() = Action.async(parse.json) { req =>
    createResource[User, CreateUserRequest](req, actors.users())
  }

  def byId(it: Long)   = Action.async {
    inquire(actors.users()) { GetById(it) } map {
      case UnknownUser  => NotFound
      case u: User      => Ok(Json.toJson(u))
    } recover {
      case NonFatal(e)  => log.error("", e); InternalServerError
    }
  }

  def byToken(it: String) = Action.async {
    inquire(actors.users()) { GetByToken(it) } map {
      case UnknownUser  => NotFound
      case u: User      => Ok(Json.toJson(u))
    } recover {
      case NonFatal(e)  => log.error("", e); InternalServerError
    }
  }

  def login() = Action.async (parse.json) { implicit r =>
    withRequest[AuthenticateRequest] { req =>
      inquire(actors.users()) { req } map {
        case UnknownUser      => NotFound
        case Failure(e)       => Forbidden(e.getMessage)
        case SuccessToken(it) => Ok(Json.toJson(it.get))
      } recover {
        case NonFatal(e)      => log.error("", e); InternalServerError
      }
    }
  }

  def resetPassword() = Action.async(parse.json) { implicit r =>
    withRequest[ResetPasswordRequest] {
      case ResetPasswordRequest(None, None) =>
        Future.successful(BadRequest)
      case req =>
        inquire(actors.users()) { req } map {
          case UnknownUser => NotFound
          case Failure(e)  => Forbidden(e.getMessage)
        } recover {
          case NonFatal(e) => log.error("", e); InternalServerError
        }
    }
  }

  def changePassword() = Action(Ok)
}