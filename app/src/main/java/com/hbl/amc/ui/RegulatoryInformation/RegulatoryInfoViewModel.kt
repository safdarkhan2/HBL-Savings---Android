package com.hbl.amc.ui.RegulatoryInformation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson

import com.hbl.amc.domain.RequestDTO.*
import com.hbl.amc.domain.RequestDTO.SaveCrsInfoDTO
import com.hbl.amc.domain.RequestDTO.SaveKYCInfoDTO
import com.hbl.amc.domain.RequestDTO.SaveDisclaimerInfoDTO
import com.hbl.amc.domain.RequestDTO.SaveFatcaInfoDTO
import com.hbl.amc.domain.RequestDTO.SaveRpqInfoDTO

import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.usecase.RegulatoryInfoUseCase
import com.hbl.amc.utils.SingleLiveEvent
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.component.KoinComponent
import java.io.File

class RegulatoryInfoViewModel(
    mainDispatcher: CoroutineDispatcher,
    ioDispatcher: CoroutineDispatcher, private val useCase: RegulatoryInfoUseCase
) : ViewModel(), KoinComponent {

    var kycInfoLookUpsApi = SingleLiveEvent<LiveDataWrapper<KYCLookupsResponse>>()
    var saveKycInfoApi = SingleLiveEvent<LiveDataWrapper<SaveKYCInfoResponse>>()
    var resumeKycApi = SingleLiveEvent<LiveDataWrapper<ResumeKYCResponse>>()
    var resumeRPQApi = SingleLiveEvent<LiveDataWrapper<ResumeRPQResponse>>()
    var resumeFatcaApi = SingleLiveEvent<LiveDataWrapper<ResumeFatcaResponse>>()
    var resumeCRSApi = SingleLiveEvent<LiveDataWrapper<ResumeCRSResponse>>()

    var crsInfoLookUpsApi = SingleLiveEvent<LiveDataWrapper<CRSInfoLookupsResponse>>()
    var saveCrsInfoApi = SingleLiveEvent<LiveDataWrapper<SaveCRSInfoResponse>>()

    var rpqInfoLookUpsApi = SingleLiveEvent<LiveDataWrapper<RpqInfoLookupsResponse>>()
    var saveRpqInfoApi = SingleLiveEvent<LiveDataWrapper<SaveRPQInfoResponse>>()

    var fatcaInfoLookUpsApi = SingleLiveEvent<LiveDataWrapper<FatcaInfoLookupsResponse>>()
    var saveFatcaInfoApi = SingleLiveEvent<LiveDataWrapper<SaveFatcaInfoResponse>>()

    var getDisclaimersApi = SingleLiveEvent<LiveDataWrapper<GetDisclaimerResponse>>()
    var saveDisclaimerApi = SingleLiveEvent<LiveDataWrapper<SaveDisclaimerResponse>>()
    var saveNextOfKinApi = SingleLiveEvent<LiveDataWrapper<SaveNextOfKinResponse>>()
    var deleteNextOfKinApi = SingleLiveEvent<LiveDataWrapper<DeleteNextOfKin>>()

    val loadingLiveData = MutableLiveData<Boolean>()

    private val job = SupervisorJob()
    private val mainUiScope = CoroutineScope(mainDispatcher + job)
    private val mainIoScope = CoroutineScope(ioDispatcher + job)
    private val gson = Gson()

    fun getCrsInfoLookups(token: String) {
        mainUiScope.launch {

            crsInfoLookUpsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getCrsInfoLookup(token = token)
                }.await()

                try {
                    crsInfoLookUpsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    crsInfoLookUpsApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                crsInfoLookUpsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveCrsInfo(token: String, saveCrsInfoDTO: SaveCrsInfoDTO) {
        mainUiScope.launch {

            saveCrsInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.saveCrsInfo(token, saveCrsInfoRequestBody(saveCrsInfoDTO))
                }.await()

                try {
                    saveCrsInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveCrsInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveCrsInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    private fun saveCrsInfoRequestBody(saveCrsInfoDTO: SaveCrsInfoDTO): RequestBody {
        val request = gson.toJson(saveCrsInfoDTO)
        return request.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }

    fun getKYCInfoLookups(token: String) {
        mainUiScope.launch {

            kycInfoLookUpsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getKYCInfoLookup(token = token)
                }.await()

                try {
                    kycInfoLookUpsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    kycInfoLookUpsApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                kycInfoLookUpsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveKYCInfo(token: String, saveKYCInfoDTO: SaveKYCInfoDTO) {
        mainUiScope.launch {

            saveKycInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.saveKYCInfo(
                        token = token,
                        body = saveKYCInfoRequestBody(saveKYCInfoDTO)
                    )
                }.await()

                try {
                    saveKycInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveKycInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveKycInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveRpqInfo(token: String, saveRpqInfoDTO: SaveRpqInfoDTO) {
        mainUiScope.launch {

            saveRpqInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.saveRpqInfo(token, saveRpqInfoRequestBody(saveRpqInfoDTO))
                }.await()

                try {
                    saveRpqInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveRpqInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveRpqInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getRpqInfoLookups(token: String) {
        mainUiScope.launch {

            rpqInfoLookUpsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getRpqInfoLookup(token)
                }.await()

                try {
                    rpqInfoLookUpsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    rpqInfoLookUpsApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                rpqInfoLookUpsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    private fun saveRpqInfoRequestBody(saveRpqInfoDTO: SaveRpqInfoDTO): RequestBody {
        val request = gson.toJson(saveRpqInfoDTO)
        return request.toRequestBody("application/json".toMediaTypeOrNull())
    }

    private fun saveKYCInfoRequestBody(saveKYCInfoDTO: SaveKYCInfoDTO): RequestBody {
        val request = gson.toJson(saveKYCInfoDTO)
        return request.toRequestBody("application/json".toMediaTypeOrNull())
    }

    fun getFatcaInfoLookups(token: String) {
        mainUiScope.launch {

            fatcaInfoLookUpsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getFatcaInfoLookup(token = token)
                }.await()

                try {
                    fatcaInfoLookUpsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    fatcaInfoLookUpsApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                fatcaInfoLookUpsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveFatcaInfo(token: String, saveFatcaInfoDTO: SaveFatcaInfoDTO) {
        mainUiScope.launch {

            saveFatcaInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.saveFatcaInfo(token, saveFatcaInfoDTO)
                }.await()

                try {
                    saveFatcaInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveFatcaInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveFatcaInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    /*private fun saveFatcaInfoRequestBody(saveFatcaInfoDTO: SaveFatcaInfoDTO): RequestBody {
        val request = gson.toJson(saveFatcaInfoDTO)
        return request.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }*/

    fun getDisclaimerInfoLookups(token: String) {
        mainUiScope.launch {

            getDisclaimersApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getDisclaimerInfoLookup(token = token)
                }.await()

                try {
                    getDisclaimersApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getDisclaimersApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getDisclaimersApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveDisclaimerInfo(token: String, saveDisclaimerInfoDTO: SaveDisclaimerInfoDTO) {
        mainUiScope.launch {

            saveDisclaimerApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.saveDisclaimerInfo(
                        token = token,
                        saveDisclaimerInfoRequestBody(
                            saveDisclaimerInfoDTO
                        )
                    )
                }.await()

                try {
                    saveDisclaimerApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveDisclaimerApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveDisclaimerApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun resumeKYC(token: String, customerID: String) {
        mainUiScope.launch {

            resumeKycApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.resumeKYC(token = token, customerID = customerID)
                }.await()

                try {
                    resumeKycApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    resumeKycApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                resumeKycApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun resumeRPQ(token: String, customerID: String) {
        mainUiScope.launch {

            resumeRPQApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.resumeRPQ(token = token, customerID = customerID)
                }.await()

                try {
                    resumeRPQApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    resumeRPQApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                resumeRPQApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun resumeFatca(token: String, customerID: String) {
        mainUiScope.launch {

            resumeFatcaApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.resumeFatca(token = token, customerId = customerID)
                }.await()

                try {
                    resumeFatcaApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    resumeFatcaApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                resumeFatcaApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun resumeCRS(token: String, customerID: String) {
        mainUiScope.launch {

            resumeCRSApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.resumeCRS(token = token, customerId = customerID)
                }.await()

                try {
                    resumeCRSApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    resumeCRSApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                resumeCRSApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveNextOfKin(
        token: String,
        customerId: MultipartBody.Part,
        name: MultipartBody.Part,
        relationship: MultipartBody.Part,
        share: MultipartBody.Part,
        nic: MultipartBody.Part,
        nicFront: MultipartBody.Part?,
        nicBack: MultipartBody.Part?,
        bForm: MultipartBody.Part?,
        nicExpiryDate: MultipartBody.Part,
        nicIssueDate: MultipartBody.Part,
        identityTypeId: MultipartBody.Part?
    ) {
        mainUiScope.launch {

            saveNextOfKinApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.saveNextOfKin(
                        token,
                        customerId,
                        name,
                        relationship,
                        share,
                        nic,
                        nicFront,
                        nicBack,
                        bForm,
                        nicExpiryDate,
                        nicIssueDate,
                        identityTypeId
                    )
                }.await()

                try {
                    saveNextOfKinApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveNextOfKinApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveNextOfKinApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun updateNextOfKin(
        token: String,
        customerId: MultipartBody.Part,
        name: MultipartBody.Part,
        relationship: MultipartBody.Part,
        share: MultipartBody.Part,
        nic: MultipartBody.Part,
        nicFront: MultipartBody.Part?,
        nicBack: MultipartBody.Part?,
        bForm: MultipartBody.Part?,
        nicExpiryDate: MultipartBody.Part,
        nicIssueDate: MultipartBody.Part,
        id: MultipartBody.Part,
        identityTypeId: MultipartBody.Part?
    ) {
        mainUiScope.launch {

            saveNextOfKinApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.updateNextOfKin(
                        token,
                        customerId,
                        name,
                        relationship,
                        share,
                        nic,
                        nicFront,
                        nicBack,
                        bForm,
                        nicExpiryDate,
                        nicIssueDate,
                        id,
                        identityTypeId
                    )
                }.await()

                try {
                    saveNextOfKinApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveNextOfKinApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveNextOfKinApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun deleteNextOfKin(token: String, id: String) {
        mainUiScope.launch {

            deleteNextOfKinApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.deleteNextOfKin(
                        token,
                        id
                    )
                }.await()

                try {
                    deleteNextOfKinApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    deleteNextOfKinApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                deleteNextOfKinApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    private fun saveDisclaimerInfoRequestBody(saveDisclaimerInfoDTO: SaveDisclaimerInfoDTO): RequestBody {
        val request = gson.toJson(saveDisclaimerInfoDTO)
        return request.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }

    private fun setLoadingVisibility(visible: Boolean) {
        loadingLiveData.postValue(visible)
    }

    override fun onCleared() {
        super.onCleared()
        this.job.cancel()
    }
}