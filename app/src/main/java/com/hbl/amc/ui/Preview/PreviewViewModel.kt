package com.hbl.amc.ui.Preview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.GetPreviewInfoResponse
import com.hbl.amc.domain.usecase.PreviewUseCase
import com.hbl.amc.utils.SingleLiveEvent
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent


class PreviewViewModel (
    mainDispatcher: CoroutineDispatcher,
    ioDispatcher: CoroutineDispatcher, private val useCase: PreviewUseCase
) : ViewModel(), KoinComponent {

    var getPreviewInfoApi = SingleLiveEvent<LiveDataWrapper<GetPreviewInfoResponse>>()

    val loadingLiveData = MutableLiveData<Boolean>()


    private val job = SupervisorJob()
    private val mainUiScope = CoroutineScope(mainDispatcher + job)
    private val mainIoScope = CoroutineScope(ioDispatcher + job)
    private val gson = Gson()

    fun getPreviewInfo(customerID: String) {
        mainUiScope.launch {

            getPreviewInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getPreviewInfo(customerID = customerID)
                }.await()

                try {
                    getPreviewInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getPreviewInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getPreviewInfoApi.value = LiveDataWrapper.error(e)
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