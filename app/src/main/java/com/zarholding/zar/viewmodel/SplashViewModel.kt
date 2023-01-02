package com.zarholding.zar.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zar.database.entity.ArticleEntity
import com.zarholding.zar.database.entity.RoleEntity
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.model.request.ArticleRequestModel
import com.zarholding.zar.model.request.NotificationUnreadCountRequestModel
import com.zarholding.zar.repository.ArticleRepository
import com.zarholding.zar.repository.NotificationRepository
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val articleRepository : ArticleRepository,
    private val tokenRepository: TokenRepository,
    private val notificationRepository: NotificationRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel(){

    private var job: Job? = null
    val errorLiveDate = SingleLiveEvent<ErrorApiModel>()
    val successLiveData = SingleLiveEvent<Int>()

    //---------------------------------------------------------------------------------------------- setError
    private suspend fun setError(response: Response<*>?) {
        withContext(Main) {
            checkResponseError(response, errorLiveDate)
        }
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- setError



    //---------------------------------------------------------------------------------------------- setError
    private suspend fun setError(message : String) {
        withContext(Main) {
            errorLiveDate.value = ErrorApiModel(EnumApiError.Error, message)
        }
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- setError



    //---------------------------------------------------------------------------------------------- getToken
    fun getToken() = tokenRepository.getToken()
    //---------------------------------------------------------------------------------------------- getToken



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
            delay(1000)
            val response = userRepository.requestUserInfo(tokenRepository.getBearerToken())
            if (response?.isSuccessful == true) {
                val userInfo = response.body()
                userInfo?.let {
                    if (it.hasError)
                        setError(it.message)
                    else {
                        it.data?.let { userInfoEntity ->
                            insertUserInfo(userInfoEntity)
                        } ?: run {
                            setError("اطلاعات خالی است")
                        }
                    }
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestUserInfo



    //---------------------------------------------------------------------------------------------- requestUserPermission
    private fun requestUserPermission() : Job {
        return CoroutineScope(IO + exceptionHandler()).launch {
            delay(1000)
            val response = userRepository.requestUserPermission(tokenRepository.getBearerToken())
            if (response?.isSuccessful == true) {
                val userPermissionResponse = response.body()
                userPermissionResponse?.let {userPermission ->
                    if (userPermission.hasError)
                        setError(userPermission.message)
                    else {
                        userPermission.data?.let { permissions ->
                            val roles : List<RoleEntity> = permissions.map { RoleEntity(it) }
                            insertUserRole(roles)
                        } ?: run {
                            setError("اطلاعات خالی است")
                        }
                    }
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else {
                setError(response)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestUserPermission



    //---------------------------------------------------------------------------------------------- requestGetArticles
    private fun requestGetArticles(request: ArticleRequestModel) : Job {
        return CoroutineScope(IO + exceptionHandler()).launch {
            delay(1000)
            val response = articleRepository.requestGetArticles(request, tokenRepository.getBearerToken())
            if (response?.isSuccessful == true) {
                val articleResponse = response.body()
                articleResponse?.let {
                    if (it.hasError)
                        setError(it.message)
                    else {
                        it.data?.let { articles ->
                            insertArticle(articles.items)
                        } ?: run {
                            setError("اطلاعات خالی است")
                        }
                    }
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else {
                setError(response)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetArticles



    //---------------------------------------------------------------------------------------------- requestGetNotificationUnreadCount
    private fun requestGetNotificationUnreadCount() : Job  {
        return CoroutineScope(IO + exceptionHandler()).launch {
            delay(1000)
            val request = NotificationUnreadCountRequestModel(
                0,
                "MIM",
                sharedPreferences.getInt(CompanionValues.notificationLastId,0)
            )
            Log.e("meri", "splash request = ${request.lastId}")
            val response = notificationRepository.requestGetNotificationUnreadCount(request)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    it.data?.let { count ->
                        Log.d("meri", "count = ${count.unreadCount} - ${count.lastId}")
                        if (count.unreadCount > 0) {
                            sharedPreferences
                                .edit()
                                .putInt(CompanionValues.notificationLastId, count.lastId)
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



    //---------------------------------------------------------------------------------------------- exceptionHandler
    private fun exceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        CoroutineScope(Main).launch {
            throwable.localizedMessage?.let { setError(it) }
        }
    }
    //---------------------------------------------------------------------------------------------- exceptionHandler


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



    //---------------------------------------------------------------------------------------------- onCleared
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onCleared

}