package com.zarholding.zar.utility

import com.zarholding.zar.database.dao.RoleDao
import com.zarholding.zar.database.dao.UserInfoDao
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class RoleManager @Inject constructor(
    private val roleDao: RoleDao,
    private val userInfoDao: UserInfoDao
) {

    //---------------------------------------------------------------------------------------------- getAdminRole
    fun getAdminRole(): Boolean {
        return !userInfoDao.getUserInfo()?.roles?.find { it == "SuperAppAdmin" }.isNullOrEmpty()
    }
    //---------------------------------------------------------------------------------------------- getAdminRole


    //---------------------------------------------------------------------------------------------- isDisableDaysAgo
    fun isDisableDaysAgo() = false
    //---------------------------------------------------------------------------------------------- isDisableDaysAgo


}