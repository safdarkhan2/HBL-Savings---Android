package com.hbl.amc.ui.self_service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hbl.amc.domain.RequestDTO.*
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.usecase.SelfServiceUseCase
import com.hbl.amc.utils.SingleLiveEvent
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.component.KoinComponent

class SelfServiceViewModel(
    mainDispatcher: CoroutineDispatcher,
    ioDispatcher: CoroutineDispatcher, private val useCase: SelfServiceUseCase
) : ViewModel(), KoinComponent {

    var getInvestmentInfoApi = SingleLiveEvent<LiveDataWrapper<GetInvestmentInfoLookupResponse>>()
    var saveInvestmentInfoApi = SingleLiveEvent<LiveDataWrapper<SaveInvestmentInfoResponse>>()
    var generateBillNumberApi = SingleLiveEvent<LiveDataWrapper<GenerateBillNumberResponse>>()


    var getRedemptionInfoApi = SingleLiveEvent<LiveDataWrapper<GetRedemptionInfoResponse>>()
    var saveRedemptionInfoApi = SingleLiveEvent<LiveDataWrapper<SaveRedemptionInfoResponse>>()

    var getConversionInfoApi = SingleLiveEvent<LiveDataWrapper<GetConversionInfoResponse>>()
    var saveConversionInfoApi = SingleLiveEvent<LiveDataWrapper<SaveConversionInfoResponse>>()

    var getPledgeInfoApi = SingleLiveEvent<LiveDataWrapper<GetPledgeInfoResponse>>()
    var savePledgeInfoApi = SingleLiveEvent<LiveDataWrapper<SavePledgeInfoResponse>>()

    val loadingLiveData = MutableLiveData<Boolean>()

    private val job = SupervisorJob()
    private val mainUiScope = CoroutineScope(mainDispatcher + job)
    private val mainIoScope = CoroutineScope(ioDispatcher + job)
    private val gson = Gson()

    fun getInvestmentInfoLookup(token:String, customerId: String) {
        mainUiScope.launch {

            getInvestmentInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getInvestmentInfoLookup(token = token, customerId = customerId)
                }.await()

                try {
                    getInvestmentInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getInvestmentInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getInvestmentInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveInvestmentInfo(
        token: String,
        customerId: MultipartBody.Part,
        transactionTypeId: MultipartBody.Part,
        amount: MultipartBody.Part,
        paymentTypeId: MultipartBody.Part,
        fundId: MultipartBody.Part,
        unitTypeId: MultipartBody.Part,
        incomeTypeId: MultipartBody.Part,
        incomeSpecifyId: MultipartBody.Part,
        incomePaymentFrequency: MultipartBody.Part,
        paymentReference: MultipartBody.Part,
        isAccepted: MultipartBody.Part,
        billNumber: MultipartBody.Part?,
        fundBankAccountNumber: MultipartBody.Part?,
        IBFTRecepit: MultipartBody.Part?,
        paymentDate: MultipartBody.Part?,
        niftStatus: MultipartBody.Part?,
        IBFTReferenceNumber: MultipartBody.Part?
    ) {
        mainUiScope.launch {

            saveInvestmentInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.saveInvestmentInfo(
                        token,
                        customerId,
                        transactionTypeId,
                        amount,
                        paymentTypeId,
                        fundId,
                        unitTypeId,
                        incomeTypeId,
                        incomeSpecifyId,
                        incomePaymentFrequency,
                        paymentReference,
                        isAccepted,
                        billNumber,
                        fundBankAccountNumber,
                        IBFTRecepit,
                        paymentDate,
                        niftStatus,
                        IBFTReferenceNumber
                    )
                }.await()

                try {
                    saveInvestmentInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveInvestmentInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveInvestmentInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getGenerateBillNumber(token: String) {
        mainUiScope.launch {

            generateBillNumberApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getGenerateBillNumber(token = token)
                }.await()

                try {
                    generateBillNumberApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    generateBillNumberApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                generateBillNumberApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getRedemptionInfoLookup(token: String, customerId: String) {
        mainUiScope.launch {

            getRedemptionInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getRedemptionInfoLookup(token = token, customerId = customerId)
                }.await()

                try {
                    getRedemptionInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getRedemptionInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getRedemptionInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveRedemptionInfo(token: String,saveRedemptionInfoDTO: SaveRedemptionInfoDTO) {
        mainUiScope.launch {

            saveRedemptionInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.saveRedemptionInfo(token = token,
                        saveRedemptionInfoRequestBody(
                            saveRedemptionInfoDTO
                        )
                    )
                }.await()

                try {
                    saveRedemptionInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveRedemptionInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveRedemptionInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    private fun saveRedemptionInfoRequestBody(saveRedemptionInfoDTO: SaveRedemptionInfoDTO): RequestBody {
        val request = gson.toJson(saveRedemptionInfoDTO)
        return request.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }

    fun getConversionInfoLookup(token: String, customerId: String) {
        mainUiScope.launch {

            getConversionInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getConversionInfoLookup(token = token, customerId = customerId)
                }.await()

                try {
                    getConversionInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getConversionInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getConversionInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveConversionInfo(token: String,saveConversionInfoDTO: SaveConversionInfoDTO) {
        mainUiScope.launch {

            saveConversionInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.saveConversionInfo(token = token,
                        saveConversionInfoRequestBody(
                            saveConversionInfoDTO
                        )
                    )
                }.await()

                try {
                    saveConversionInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveConversionInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveConversionInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    private fun saveConversionInfoRequestBody(saveConversionInfoDTO: SaveConversionInfoDTO): RequestBody {
        val request = gson.toJson(saveConversionInfoDTO)
        return request.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }

    fun getPledgeInfoLookup(token: String, customerId: String) {
        mainUiScope.launch {

            getPledgeInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getPledgeInfoLookup(token = token, customerId = customerId)
                }.await()

                try {
                    getPledgeInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getPledgeInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getPledgeInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun savePledgeInfo(token: String, savePledgeInfoDTO: SavePledgeInfoDTO) {
        mainUiScope.launch {

            savePledgeInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.savePledgeInfo(token = token, savePledgeInfoDTO)
                }.await()

                try {
                    savePledgeInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    savePledgeInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                savePledgeInfoApi.value = LiveDataWrapper.error(e)
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