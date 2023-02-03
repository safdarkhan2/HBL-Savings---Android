package com.hbl.amc.ui.Disclaimers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.DocumentChecklistResponse
import com.hbl.amc.domain.model.SampleDownloadResponse
import com.hbl.amc.domain.model.UploadDocumentResponse
import com.hbl.amc.domain.usecase.DisclaimersUseCase
import com.hbl.amc.utils.SingleLiveEvent
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import org.koin.core.component.KoinComponent

class DisclaimersViewModel(
    mainDispatcher: CoroutineDispatcher,
    ioDispatcher: CoroutineDispatcher, private val useCase: DisclaimersUseCase
) : ViewModel(), KoinComponent {

    var documentsChecklistApi = SingleLiveEvent<LiveDataWrapper<DocumentChecklistResponse>>()
    var uploadDocumentApi = SingleLiveEvent<LiveDataWrapper<UploadDocumentResponse>>()
    var downloadDocumentApi = SingleLiveEvent<LiveDataWrapper<SampleDownloadResponse>>()
    val loadingLiveData = MutableLiveData<Boolean>()


    private val job = SupervisorJob()
    private val mainUiScope = CoroutineScope(mainDispatcher + job)
    private val mainIoScope = CoroutineScope(ioDispatcher + job)
    private val gson = Gson()

    fun getCustomerDocumentChecklist(customerID : String) {
        mainUiScope.launch {

            documentsChecklistApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getCustomerDocumentChecklist(customerID)
                }.await()

                try {
                    documentsChecklistApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    documentsChecklistApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                documentsChecklistApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun uploadDocumentByIds(docFile : MultipartBody.Part, docTypeId: MultipartBody.Part, customerID : MultipartBody.Part?) {
        mainUiScope.launch {

            uploadDocumentApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.uploadDocument(docFile, docTypeId, customerID)
                }.await()

                try {
                    uploadDocumentApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    uploadDocumentApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                uploadDocumentApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getSampleDocument(docId: Int) {
        mainUiScope.launch {

            downloadDocumentApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getSampleDocument(docId)
                }.await()

                try {
                    downloadDocumentApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    downloadDocumentApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                downloadDocumentApi.value = LiveDataWrapper.error(e)
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