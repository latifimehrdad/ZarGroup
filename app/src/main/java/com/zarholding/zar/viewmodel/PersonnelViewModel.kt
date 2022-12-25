package com.zarholding.zar.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.model.request.FilterUserRequestModel
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

@HiltViewModel
class PersonnelViewModel @Inject constructor(
    private val repository: UserRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private var job: Job? = null
    val userMutableLiveData = SingleLiveEvent<List<UserInfoEntity>>()

    //---------------------------------------------------------------------------------------------- requestGetUser
    fun requestGetUser(search: String) {
        job = CoroutineScope(IO).launch {
            val response = repository.requestGetUser(search, tokenRepository.getBearerToken())
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (!it.hasError)
                        it.data?.let { models ->
                            models.items?.let { list ->
                                withContext(Main){
                                    userMutableLiveData.value = list
                                }
                            }
                        }
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetUser


    //---------------------------------------------------------------------------------------------- getFilterUserRequestModel
    fun getFilterUserRequestModel(): FilterUserRequestModel = repository.filterUser
    //---------------------------------------------------------------------------------------------- getFilterUserRequestModel


    //---------------------------------------------------------------------------------------------- onCleared
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onCleared

}