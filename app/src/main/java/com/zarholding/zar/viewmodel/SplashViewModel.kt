package com.zarholding.zar.viewmodel

import android.content.SharedPreferences
import com.zarholding.zar.database.entity.ArticleEntity
import com.zarholding.zar.database.entity.RoleEntity
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.hilt.ResourcesProvider
import com.zarholding.zar.model.request.ArticleRequestModel
import com.zarholding.zar.model.request.NotificationUnreadCountRequestModel
import com.zarholding.zar.repository.ArticleRepository
import com.zarholding.zar.repository.NotificationRepository
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import zar.R
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val articleRepository : ArticleRepository,
    private val notificationRepository: NotificationRepository,
    private val sharedPreferences: SharedPreferences,
    private val resourcesProvider: ResourcesProvider
) : ZarViewModel(){

    val successLiveData = SingleLiveEvent<Int>()

    //---------------------------------------------------------------------------------------------- userIsEntered
    fun userIsEntered() = userRepository.isEntered()
    //---------------------------------------------------------------------------------------------- userIsEntered


    //---------------------------------------------------------------------------------------------- requestGetData
    fun requestGetData() {
        job?.cancel()
        job = CoroutineScope(IO).launch {
            delay(1000)
            requestUserInfo().join()
            requestUserPermission().join()
            val requestSlide = ArticleRequestModel(
                1,
                100,
                "",
                false,
                "SlideShow"
            )
            requestGetArticles(requestSlide).join()
            val requestArticle = ArticleRequestModel(
                1,
                100,
                "",
                false,
                "Article"
            )
            requestGetArticles(requestArticle).join()
            requestGetNotificationUnreadCount().join()
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetData


    //---------------------------------------------------------------------------------------------- requestUserInfo
    private fun requestUserInfo() : Job {
        return CoroutineScope(IO + exceptionHandler()).launch {
            delay(500)
            val response = userRepository.requestUserInfo()
            if (response?.isSuccessful == true) {
                val userInfo = response.body()
                userInfo?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else {
                        it.data?.let { userInfoEntity ->
                            insertUserInfo(userInfoEntity)
                        } ?: run {
                            setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                        }
                    }
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestUserInfo


    //---------------------------------------------------------------------------------------------- requestUserPermission
    private fun requestUserPermission() : Job {
        return CoroutineScope(IO + exceptionHandler()).launch {
            delay(500)
            val response = userRepository.requestUserPermission()
            if (response?.isSuccessful == true) {
                val userPermissionResponse = response.body()
                userPermissionResponse?.let {userPermission ->
                    if (userPermission.hasError)
                        setMessage(userPermission.message)
                    else {
                        userPermission.data?.let { permissions ->
                            val roles : List<RoleEntity> = permissions.map { RoleEntity(it) }
                            insertUserRole(roles)
                        } ?: run {
                            setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                        }
                    }
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                }
            } else {
                setMessage(response)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestUserPermission


    //---------------------------------------------------------------------------------------------- requestGetArticles
    private fun requestGetArticles(request: ArticleRequestModel) : Job {
        return CoroutineScope(IO + exceptionHandler()).launch {
            delay(500)
            val response = articleRepository.requestGetArticles(request)
            if (response?.isSuccessful == true) {
                val articleResponse = response.body()
                articleResponse?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else {
                        it.data?.let { articles ->
                            insertArticle(articles.items)
                        } ?: run {
                            setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                        }
                    }
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                }
            } else {
                setMessage(response)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetArticles


    //---------------------------------------------------------------------------------------------- requestGetNotificationUnreadCount
    private fun requestGetNotificationUnreadCount() : Job  {
        return CoroutineScope(IO + exceptionHandler()).launch {
            delay(500)
            val request = NotificationUnreadCountRequestModel(
                0,
                "MIM",
                sharedPreferences.getInt(CompanionValues.notificationLast,0)
            )
            val response = notificationRepository.requestGetNotificationUnreadCount(request)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    it.data?.let { count ->
                        if (count.unreadCount > 0) {
                            sharedPreferences
                                .edit()
                                .putInt(CompanionValues.notificationLast, count.lastId)
                                .apply()
                        }
                        withContext(Main){
                            successLiveData.value = count.unreadCount
                        }
                    }
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetNotificationUnreadCount


    //---------------------------------------------------------------------------------------------- insertUserInfo
    private fun insertUserInfo(user: UserInfoEntity) {
        userRepository.insertUserInfo(user)
    }
    //---------------------------------------------------------------------------------------------- insertUserInfo


    //---------------------------------------------------------------------------------------------- insertUserRole
    private fun insertUserRole(roles: List<RoleEntity>) {
        userRepository.insertUserRole(roles)
    }
    //---------------------------------------------------------------------------------------------- insertUserRole


    //---------------------------------------------------------------------------------------------- insertArticle
    private fun insertArticle(items: List<ArticleEntity?>) {
        articleRepository.insertArticle(items)
    }
    //---------------------------------------------------------------------------------------------- insertArticle

}