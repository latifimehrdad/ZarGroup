package com.zarholding.zar.utility

import com.zarholding.zar.database.dao.RoleDao
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class RoleManager @Inject constructor(private val roleDao: RoleDao){

    //---------------------------------------------------------------------------------------------- getAdminRole
    fun getAdminRole(roles : List<String>?) : Boolean {
        return !roles?.find { it == "SuperAppAdmin" }.isNullOrEmpty()
    }
    //---------------------------------------------------------------------------------------------- getAdminRole

}