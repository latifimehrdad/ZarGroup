package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.extensions.persianNumberToEnglishNumber
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.database.dao.RoleDao
import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.database.entity.RoleEntity
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.model.request.FilterUserRequestModel
import com.zarholding.zar.model.response.user.UserResponseModel
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

class UserRepository @Inject constructor(
    private val api: ApiSuperApp,
    private val userInfoDao: UserInfoDao,
    private val roleDao: RoleDao,
) {

    private var pageNumber = 0
    private val pageSize = 20
    val filterUser = FilterUserRequestModel(pageNumber, pageSize, "")


    //---------------------------------------------------------------------------------------------- requestUserInfo
    suspend fun requestUserInfo(token: String) =
        apiCall{ api.requestUserInfo(token) }
    //---------------------------------------------------------------------------------------------- requestUserInfo


    //---------------------------------------------------------------------------------------------- requestUserPermission
    suspend fun requestUserPermission(token: String) =
        apiCall{ api.requestUserPermission(token) }
    //---------------------------------------------------------------------------------------------- requestUserPermission


    //---------------------------------------------------------------------------------------------- requestGetUser
    suspend fun requestGetUser(search: String, token: String) : Response<UserResponseModel>? {
        filterUser.Search = search.persianNumberToEnglishNumber()
        filterUser.PageNumber++
        return apiCall{ api.requestGetUser(filterUser, token) }
    }
    //---------------------------------------------------------------------------------------------- requestGetUser


    //---------------------------------------------------------------------------------------------- insertUserInfo
    fun insertUserInfo(user: UserInfoEntity) {
        userInfoDao.insertUserInfo(user)
    }
    //---------------------------------------------------------------------------------------------- insertUserInfo



    //---------------------------------------------------------------------------------------------- getUser
    fun getUser(): UserInfoEntity? {
        return userInfoDao.getUserInfo()
    }
    //---------------------------------------------------------------------------------------------- getUser


    //---------------------------------------------------------------------------------------------- isAdministrativeUser
    fun isAdministrativeUser() = getUser()?.isAdministrative?:false
    //---------------------------------------------------------------------------------------------- isAdministrativeUser


    //---------------------------------------------------------------------------------------------- isDriver
    fun isDriver() = getUser()?.isDriver?:false
    //---------------------------------------------------------------------------------------------- isDriver



    //---------------------------------------------------------------------------------------------- insertUserRole
    fun insertUserRole(roles: List<RoleEntity>) {
        roleDao.deleteAllRecord()
        roleDao.insert(roles)
    }
    //---------------------------------------------------------------------------------------------- insertUserRole


}