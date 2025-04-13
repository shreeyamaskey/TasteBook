package server.com.dao.user

import org.jetbrains.exposed.sql.ResultRow
import server.com.dao.DatabaseFactory.dbQuery
import server.com.models.SignUpParams
import org.jetbrains.exposed.sql.*
import server.com.security.hashPassword
import java.time.Instant

class UserDaoImpl : UserDao {
    override suspend fun insert(params: SignUpParams): User? {
        return dbQuery{
            val insertStatement = UserTable.insert {
//                it[id] = IdGenerator.generateId()
                it[firstName] = params.firstName
                it[lastName] = params.lastName
                it[username] = params.username
                it[email] = params.email
                it[password] = hashPassword(params.password)
                it[createdAt] = Instant.now().toEpochMilli()
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

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[UserTable.id],
            firstName = row[UserTable.firstName],
            lastName = row[UserTable.lastName],
            username = row[UserTable.username],
            password = row[UserTable.password],
            email = row[UserTable.email],
            createdAt = row[UserTable.createdAt],
            imageUrl = row[UserTable.imageUrl]
        )
    }
}