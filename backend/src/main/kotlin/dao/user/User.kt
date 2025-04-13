package server.com.dao.user

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object UserTable: Table(name = "users"){
    val id = integer(name = "user_id").autoIncrement()
    val firstName = varchar(name = "first_name", length = 250)
    val lastName = varchar(name = "last_name", length = 250)
    val username = varchar(name = "username", length = 250).uniqueIndex()
    val email = varchar(name = "user_email", length = 250).uniqueIndex()
    val password = varchar(name = "user_password", length = 100)
    val createdAt = long(name = "created_at")
    val imageUrl = text(name = "image_url").nullable()
//    val friendsCount = integer(name = "friends_count").default(defaultValue = 0)


    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}


@Serializable
data class User (
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String,
    val createdAt: Long,
    val imageUrl: String?
)
