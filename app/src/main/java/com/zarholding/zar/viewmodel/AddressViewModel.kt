package com.zarholding.zar.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zarholding.zar.model.response.address.AddressSuggestionModel
import com.zarholding.zar.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(private val repo: AddressRepository) : ViewModel() {

    private var job: Job? = null
    val addressSuggestionLiveData = MutableLiveData<List<AddressSuggestionModel>>()


    //---------------------------------------------------------------------------------------------- requestGetSuggestionAddress
    fun requestGetSuggestionAddress(address: String, suggestion: List<AddressSuggestionModel>?) {
        job = CoroutineScope(IO).launch {
            val response = repo.requestGetSuggestionAddress(address, suggestion)
            if (response?.isSuccessful == true) {
                response.body()?.let { list ->
                    addressSuggestionLiveData.value = list
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetSuggestionAddress



    //---------------------------------------------------------------------------------------------- onCleared
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onCleared


}