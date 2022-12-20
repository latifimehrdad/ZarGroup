package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zarholding.zar.model.enum.EnumAdminTaxiType
import com.zarholding.zar.repository.TaxiRepository
import com.zarholding.zar.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminTaxiListViewModel @Inject constructor(
    private val repository: TaxiRepository
) : ViewModel() {

    @Inject
    lateinit var tokenRepository: TokenRepository

    private var enumAdminTaxiType: EnumAdminTaxiType? = null


    //---------------------------------------------------------------------------------------------- setEnumAdminTaxiType
    fun setEnumAdminTaxiType(type: String) {
        when (type) {
            EnumAdminTaxiType.REQUEST.name -> enumAdminTaxiType = EnumAdminTaxiType.REQUEST
            EnumAdminTaxiType.HISTORY.name -> enumAdminTaxiType = EnumAdminTaxiType.HISTORY
        }
    }
    //---------------------------------------------------------------------------------------------- setEnumAdminTaxiType


    //---------------------------------------------------------------------------------------------- getEnumAdminTaxiType
    fun getEnumAdminTaxiType() = enumAdminTaxiType
    //---------------------------------------------------------------------------------------------- getEnumAdminTaxiType



    //---------------------------------------------------------------------------------------------- getTaxiList
    fun getTaxiList() = when(enumAdminTaxiType!!) {
        EnumAdminTaxiType.REQUEST ->  requestTaxiList()
        EnumAdminTaxiType.HISTORY -> requestMyTaxiRequestList()
    }
    //---------------------------------------------------------------------------------------------- getTaxiList



    //---------------------------------------------------------------------------------------------- requestTaxiList
    private fun requestTaxiList() =
        repository.requestTaxiList(tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestTaxiList


    //---------------------------------------------------------------------------------------------- requestMyTaxiRequestList
    private fun requestMyTaxiRequestList() =
        repository.requestMyTaxiRequestList(tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestMyTaxiRequestList

}