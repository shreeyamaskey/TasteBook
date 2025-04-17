package server.com.dao.user

import org.jetbrains.exposed.sql.ResultRow
import server.com.dao.DatabaseFactory.dbQuery
import server.com.models.SignUpParams
import org.jetbrains.exposed.sql.*
import server.com.models.UpdateUserParams
import server.com.security.hashPassword
import java.time.ZoneId
import kotlinx.datetime.Instant


class UserDaoImpl : UserDao {
    override suspend fun insert(params: SignUpParams): User? {
        return dbQuery{
            val insertStatement = UserTable.insert {
                it[firstName] = params.firstName
                it[lastName] = params.lastName
                it[username] = params.username
                it[email] = params.email
                it[password] = hashPassword(params.password)
            }

            insertStatement.resultedValues?.singleOrNull()?.let {
                rowToUser(it)
            }
        }
    }

    override suspend fun findByEmail(email: String): User? {
        return dbQuery {
            UserTable.select { UserTable.email eq email}
                .map {rowToUser(it)}
                .singleOrNull()
        }
    }

    override suspend fun findById(id: Int): User? {
        return dbQuery {
            UserTable.select { UserTable.id eq id }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    override suspend fun update(id: Int, params: UpdateUserParams): User? {
        return dbQuery {
            UserTable.update({ UserTable.id eq id }) {
                params.firstName?.let { firstName -> it[UserTable.firstName] = firstName }
                params.lastName?.let { lastName -> it[UserTable.lastName] = lastName }
                params.username?.let { username -> it[UserTable.username] = username }
                params.email?.let { email -> it[UserTable.email] = email }
                params.password?.let { password -> it[UserTable.password] = password }
                params.avatar?.let { avatar -> it[UserTable.imageUrl] = avatar }
            }
            
            findById(id)
        }
    }

    private fun rowToUser(row: ResultRow): User {
        val localDateTime = row[UserTable.createdAt]
        // Use the system default time zone to convert LocalDateTime to java.time.Instant
        val javaInstant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        // Convert the java.time.Instant to a kotlinx.datetime.Instant using the epoch milliseconds.
        val kotlinInstant = Instant.fromEpochMilliseconds(javaInstant.toEpochMilli())

        return User(
            id = row[UserTable.id],
            firstName = row[UserTable.firstName],
            lastName = row[UserTable.lastName],
            username = row[UserTable.username],
            password = row[UserTable.password],
            email = row[UserTable.email],
            createdAt = kotlinInstant,
            imageUrl = row[UserTable.imageUrl]
        )
    }
}