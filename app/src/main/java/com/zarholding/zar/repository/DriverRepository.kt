package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.enum.EnumDriverType
import javax.inject.Inject

class DriverRepository @Inject constructor(
    private val api: ApiSuperApp,
    private val tokenRepository: TokenRepository
) {

    //---------------------------------------------------------------------------------------------- requestGetDriver
    suspend fun requestGetDriver(type : EnumDriverType,companyCode : String?) =
        apiCall { api.requestGetDriver(type, companyCode, tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestGetDriver

}