package com.sm.tastebook.data.common.datastore

import com.sm.tastebook.domain.user.model.UserAuthResultData
import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val id: Int = -1,
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val avatar: String? = null,
    val token: String = ""
)

fun UserSettings.toUserAuthResultData() = UserAuthResultData(
    id = id,  // This mapping is crucial
    firstName = firstName,
    lastName = lastName,
    username = username,
    email = email,
    avatar = avatar,
    token = token
)

fun UserAuthResultData.toUserSettings() : UserSettings{
    return UserSettings(id, firstName, lastName, username, email, avatar, token)
}