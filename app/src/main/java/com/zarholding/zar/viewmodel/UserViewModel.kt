package com.zarholding.zar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zar.core.tools.extensions.persianNumberToEnglishNumber
import com.zarholding.zar.database.dao.RoleDao
import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.database.entity.RoleEntity
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.model.request.FilterUserRequestModel
import com.zarholding.zar.model.response.user.UserResponseModel
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val userInfoDao: UserInfoDao,
    private val roleDao: RoleDao,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private var pageNumber = 0
    private val pageSize = 10
    val filterUser = FilterUserRequestModel(pageNumber, pageSize, "")

    //---------------------------------------------------------------------------------------------- requestUserInfo
    fun requestUserInfo() = repository.requestUserInfo(tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestUserInfo


    //---------------------------------------------------------------------------------------------- requestUserPermission
    fun requestUserPermission() = repository.requestUserPermission(tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestUserPermission


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


    //---------------------------------------------------------------------------------------------- requestGetUser
    fun requestGetUser(search: String) : LiveData<UserResponseModel?> {
        filterUser.Search = search.persianNumberToEnglishNumber()
        filterUser.PageNumber++
        return repository.requestGetUser(filterUser, tokenRepository.getBearerToken())
    }
    //---------------------------------------------------------------------------------------------- requestGetUser


    //---------------------------------------------------------------------------------------------- insertUserRole
    fun insertUserRole(roles: List<RoleEntity>) {
        roleDao.deleteAllRecord()
        roleDao.insert(roles)
    }
    //---------------------------------------------------------------------------------------------- insertUserRole


    //---------------------------------------------------------------------------------------------- getToken
    fun getToken() = tokenRepository.getToken()
    //---------------------------------------------------------------------------------------------- getToken


}