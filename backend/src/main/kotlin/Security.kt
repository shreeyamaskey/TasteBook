package server.com

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import org.koin.ktor.ext.inject
import server.com.dao.user.UserDao
import server.com.models.AuthResponse

private val jwtAudience = System.getenv("jwt.audience")
private val jwtIssuer = System.getenv("jwt.domain")
private val jwtSecret = System.getenv("jwt.secret")

private const val EMAIL_CLAIM = "email"
private const val USER_ID_CLAIM = "userId"

fun Application.configureSecurity() {
    val userDao by inject<UserDao>()

    authentication {
        jwt {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim(EMAIL_CLAIM).asString() != null) {
                    val userExists = userDao.findByEmail(email = credential.payload.getClaim(EMAIL_CLAIM).asString()) != null
                    val isValidAudience = credential.payload.audience.contains(jwtAudience)
                    if (userExists && isValidAudience) {
                        JWTPrincipal(payload = credential.payload)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = AuthResponse(
                        errorMessage = "Token is not valid or has expired"
                    )
                )
            }
        }
    }
}

fun generateToken(email: String, userId: Int): String {
    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaim(EMAIL_CLAIM, email)
        .withClaim(USER_ID_CLAIM, userId)
        //.withExpiresAt()
        .sign(Algorithm.HMAC256(jwtSecret))
}
