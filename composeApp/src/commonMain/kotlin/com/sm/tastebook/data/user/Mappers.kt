package com.sm.tastebook.data.user

import com.sm.tastebook.domain.user.model.UserAuthResultData

internal fun AuthResponseData.toAuthResultData(): UserAuthResultData{
    return UserAuthResultData(id, firstName, lastName, username, email, avatar, token)
}