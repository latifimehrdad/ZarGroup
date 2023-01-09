package com.zarholding.zar.viewmodel

import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.model.other.ShowImageModel
import com.zarholding.zar.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val userInfoDao: UserInfoDao
) : ZarViewModel() {


    //---------------------------------------------------------------------------------------------- getBearerToken
    fun getBearerToken() = tokenRepository.getBearerToken()
    //---------------------------------------------------------------------------------------------- getBearerToken


    //---------------------------------------------------------------------------------------------- getUserInfo
    fun getUserInfo() = userInfoDao.getUserInfo()
    //---------------------------------------------------------------------------------------------- getUserInfo


    //---------------------------------------------------------------------------------------------- getModelForShowImageProfile
    fun getModelForShowImageProfile() : ShowImageModel {
        val model = ShowImageModel("", null, null)
        getUserInfo()?.userName?.let {
            model.imageName = it
        }
        model.token = tokenRepository.getBearerToken()
        return model
    }
    //---------------------------------------------------------------------------------------------- getModelForShowImageProfile

}