package com.hbl.amc.ui.Start

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hbl.amc.domain.RequestDTO.ForgotPasswordDTO
import com.hbl.amc.domain.RequestDTO.LoginDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.usecase.GenericUseCase
import com.hbl.amc.domain.usecase.LoginUsecase
import com.hbl.amc.utils.SingleLiveEvent
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent

class LoginViewModel(
    mainDispatcher: CoroutineDispatcher,
    ioDispatcher: CoroutineDispatcher, private val useCase: LoginUsecase
) : ViewModel(), KoinComponent {
    var loginApi = SingleLiveEvent<LiveDataWrapper<LoginResponse>>()
    var forgotPasswordApi = SingleLiveEvent<LiveDataWrapper<ForgotPasswordResponse>>()

    val loadingLiveData = MutableLiveData<Boolean>()
    private val job = SupervisorJob()
    private val mainUiScope = CoroutineScope(mainDispatcher + job)
    private val mainIoScope = CoroutineScope(ioDispatcher + job)

    fun loginUser(loginDTO: LoginDTO) {
        mainUiScope.launch {

            loginApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.loginUser(loginDTO)
                }.await()

                try {
                    loginApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    loginApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                loginApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun forgotPassword(forgotPasswordDTO: ForgotPasswordDTO) {
        mainUiScope.launch {

            forgotPasswordApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.forgotPassword(forgotPasswordDTO)
                }.await()

                try {
                    forgotPasswordApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    forgotPasswordApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                forgotPasswordApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    private fun setLoadingVisibility(visible: Boolean) {
        loadingLiveData.postValue(visible)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}