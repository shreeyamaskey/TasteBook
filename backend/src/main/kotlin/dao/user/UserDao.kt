package server.com.dao.user

import server.com.models.SignUpParams
import server.com.models.UpdateUserParams

interface UserDao {
    suspend fun insert(params: SignUpParams): User?
    suspend fun findByEmail(email: String): User?
    suspend fun findById(id: Int): User?
    suspend fun update(id: Int, params: UpdateUserParams): User?
}