package store

import java.sql.Timestamp
import java.util.Date

import domain._
import domain.collections._
import services.AppServices
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.language.postfixOps

trait Permissions extends ObjectStore[Permission, AssignPermissionRequest] {}

class DatabasePermissions(services: AppServices, db: Database) extends Permissions {

  implicit val ec = services.ec()

  override def byId(id: Long): Future[Option[Permission]] = Future.failed(new Exception("TODO"))

  override def create(request: AssignPermissionRequest): Future[Permission] = {

    val instant   = services.clock().instant()
    val created   = new Timestamp(instant.toEpochMilli)
    db.run {
      (permissions returning permissions.map(_.id)) += (
        0l,
        request.user,
        created,
        None,
        request.permission
      )
    } map { id =>
      Permission(
        id        = id,
        user      = request.user,
        created   = Date.from(instant),
        deleted   = None,
        name      = request.permission
      )
    }
  }
}