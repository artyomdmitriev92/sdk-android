package com.sdk.pushexpress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdk.sdkpushexpress.models.DeviceConfigRequest
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.sdk.sdkpushexpress.repository.ApiRepository

class MainViewModel(private val api: ApiRepository) : ViewModel() {

    val code: MutableLiveData<String> = MutableLiveData()
    val request: MutableLiveData<DeviceConfigRequest> = MutableLiveData()
    val showProgress: MutableLiveData<Boolean> = MutableLiveData()
    private val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
        (exception as? HttpException)?.let {
            code.postValue(it.code().toString())
            showProgress.postValue(false)
            return@CoroutineExceptionHandler
        }
        code.postValue("No http code, other error")
        showProgress.postValue(false)
    }

    fun onButtonClick(fireBaseToken: String) = viewModelScope.launch(handler + Dispatchers.IO) {
        showProgress.postValue(true)
        val res = api.sendDeviceConfigDebug(SDKEXPRESS_TEST_ID, fireBaseToken)
        request.postValue(res.createdRequest)
        showProgress.postValue(false)
    }

    companion object {
        private const val SDKEXPRESS_TEST_ID = "1234-100500"
    }
}