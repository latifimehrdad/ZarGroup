package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.request.NotificationUnreadCountRequestModel
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val api: ApiSuperApp,
    private val tokenRepository: TokenRepository
) {

    //---------------------------------------------------------------------------------------------- requestGetNotificationUnreadCount
    suspend fun requestGetNotificationUnreadCount(request : NotificationUnreadCountRequestModel) =
        apiCall { api.requestGetNotificationUnreadCount(
            request, tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestGetNotificationUnreadCount



    //---------------------------------------------------------------------------------------------- requestGetNotification
    suspend fun requestGetNotification() =
        apiCall { api.requestGetNotification(tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestGetNotification


    //---------------------------------------------------------------------------------------------- requestReadNotification
    suspend fun requestReadNotification(ids : List<Int>) =
        apiCall { api.requestReadNotification(ids, tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestReadNotification

}