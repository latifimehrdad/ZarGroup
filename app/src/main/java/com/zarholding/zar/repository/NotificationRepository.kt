package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zar.api.ApiSuperApp
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val api: ApiSuperApp,
    private val tokenRepository: TokenRepository
) {

    //---------------------------------------------------------------------------------------------- requestGetNotificationUnreadCount
    suspend fun requestGetNotificationUnreadCount() =
        apiCall { api.requestGetNotificationUnreadCount(tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestGetNotificationUnreadCount

}