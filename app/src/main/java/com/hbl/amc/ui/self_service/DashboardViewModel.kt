package com.hbl.amc.ui.self_service

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.GetMutualFundDashboardResponse
import com.hbl.amc.domain.model.GetProfileInfoResponse
import com.hbl.amc.domain.model.VPSDashboardResponse
import com.hbl.amc.domain.usecase.DashboardUseCase
import com.hbl.amc.utils.SingleLiveEvent
import kotlinx.coroutines.*
import okhttp3.Response
import org.koin.core.component.KoinComponent

class DashboardViewModel(
    mainDispatcher: CoroutineDispatcher,
    ioDispatcher: CoroutineDispatcher, private val useCase: DashboardUseCase
) : ViewModel(), KoinComponent {

    var getMutualFundDashboardApi = SingleLiveEvent<LiveDataWrapper<GetMutualFundDashboardResponse>>()
//    var getMutualAccountStatementApi = SingleLiveEvent<LiveDataWrapper<Response>>()

    var getProfileInfoApi = SingleLiveEvent<LiveDataWrapper<GetProfileInfoResponse>>()

    var getVPSFundDashboardApi = SingleLiveEvent<LiveDataWrapper<VPSDashboardResponse>>()

    val loadingLiveData = MutableLiveData<Boolean>()


    private val job = SupervisorJob()
    private val mainUiScope = CoroutineScope(mainDispatcher + job)
    private val mainIoScope = CoroutineScope(ioDispatcher + job)
    private val gson = Gson()

    fun getMutualFundsDashboard(token: String, customerID: Int, duration: Int) {
        mainUiScope.launch {

            getMutualFundDashboardApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getMutualFundDashboard(
                        token = token,
                        customerID = customerID,
                        duration = duration
                    )
                }.await()

                try {
                    getMutualFundDashboardApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getMutualFundDashboardApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getMutualFundDashboardApi.value = LiveDataWrapper.error(e)
            }
        }
    }

//    fun getMutualFundsAccountStatement(token: String, mutualFundAccountStatementDTO: MutualFundAccountStatementDTO) {
//        mainUiScope.launch {
//
//            getMutualAccountStatementApi.value = LiveDataWrapper.loading()
//            setLoadingVisibility(true)
//
//            try {
//                val data = mainIoScope.async {
//                    return@async useCase.getMutualFundAccountStatement(
//                        token = token,
//                        mutualFundAccountStatementDTO
//                    )
//                }.await()
//
//                try {
//                    getMutualAccountStatementApi.value = LiveDataWrapper.success(data)
//                } catch (e: Exception) {
//                    setLoadingVisibility(false)
//                    getMutualAccountStatementApi.value = LiveDataWrapper.error(e)
//                }
//
//                setLoadingVisibility(false)
//
//            } catch (e: Exception) {
//                setLoadingVisibility(false)
//                getMutualAccountStatementApi.value = LiveDataWrapper.error(e)
//            }
//        }
//    }

    fun getProfileInfo(token: String, customerID: String) {
        mainUiScope.launch {

            getProfileInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getProfileInfo(
                        token = token,
                        customerID = customerID
                    )
                }.await()

                try {
                    getProfileInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getProfileInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getProfileInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getVPSFundDashboard(
        token: String,
        customerID: String,
        duration: String
    ) {
        mainUiScope.launch {

            getVPSFundDashboardApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getVPSFundDashboard(
                        token = token,
                        customerID = customerID,
                        duration = duration
                    )
                }.await()

                try {
                    getVPSFundDashboardApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getVPSFundDashboardApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getVPSFundDashboardApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    private fun setLoadingVisibility(visible: Boolean) {
        loadingLiveData.postValue(visible)
    }

    override fun onCleared() {
        super.onCleared()
        this.job.cancel()
    }
}