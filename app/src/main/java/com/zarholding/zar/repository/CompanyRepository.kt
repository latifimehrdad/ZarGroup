package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zar.api.ApiSuperApp
import javax.inject.Inject

class CompanyRepository @Inject constructor(
    private val api : ApiSuperApp,
    private val tokenRepository: TokenRepository
) {

    //---------------------------------------------------------------------------------------------- requestGetCompanies
    suspend fun requestGetCompanies() =
        apiCall { api.requestGetCompanies(tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestGetCompanies

}