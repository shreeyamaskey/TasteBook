package server.com.dao.user

import server.com.models.SignUpParams

interface UserDao {
    suspend fun insert(params: SignUpParams): User?
    suspend fun findByEmail(email: String): User?
}