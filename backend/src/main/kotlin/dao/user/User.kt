package server.com.dao.user

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import server.com.dao.recipe.RecipeTable.defaultExpression
import kotlinx.datetime.Instant


object UserTable: Table(name = "users"){
    val id = integer(name = "user_id").autoIncrement().uniqueIndex()
    val firstName = varchar(name = "first_name", length = 250)
    val lastName = varchar(name = "last_name", length = 250)
    val username = varchar(name = "username", length = 250).uniqueIndex()
    val email = varchar(name = "user_email", length = 250).uniqueIndex()
    val password = varchar(name = "user_password", length = 100)
    val createdAt = datetime(name = "created_at").defaultExpression(defaultValue = CurrentDateTime)
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
    val createdAt: Instant,
    //fix the instant time translation
    val imageUrl: String?
)
