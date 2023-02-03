package com.hbl.amc.ui.CustomerInformation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hbl.amc.domain.RequestDTO.*
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.usecase.CustomerInfoUseCase
import com.hbl.amc.utils.SingleLiveEvent
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.component.KoinComponent
import java.io.File

class CustomerInfoViewModel(
    mainDispatcher: CoroutineDispatcher,
    ioDispatcher: CoroutineDispatcher, private val useCase: CustomerInfoUseCase
) : ViewModel(), KoinComponent {

    val resendOTPApi = SingleLiveEvent<LiveDataWrapper<SendOTPResponse>>()
    val verifyOTPApi = SingleLiveEvent<LiveDataWrapper<VerifyOTPResponse>>()

    val scanFrontIdCardApi = SingleLiveEvent<LiveDataWrapper<CnicUploadResponse>>()
    val scanBackIdCardApi = SingleLiveEvent<LiveDataWrapper<CnicUploadResponse>>()

    var resumeProfessionalInfoApi = SingleLiveEvent<LiveDataWrapper<ResumeProfessionalInfoRes>>()
    var resumeBankInfoApi = SingleLiveEvent<LiveDataWrapper<ResumeBankInfoRes>>()
    var personalInfoLookUpsApi = SingleLiveEvent<LiveDataWrapper<PersonalInfoLookupsResponse>>()
    var savePersonalInfoApi = SingleLiveEvent<LiveDataWrapper<SavePersonalInfoResponse>>()
    var changeAccountTypeApi = SingleLiveEvent<LiveDataWrapper<SavePersonalInfoResponse>>()
    var resumePersonalInfoApi = SingleLiveEvent<LiveDataWrapper<ResumePersonalInfo>>()
    var accountTypesApi = SingleLiveEvent<LiveDataWrapper<AccountTypesResponse>>()

    var professionalInfoLookUpsApi =
        SingleLiveEvent<LiveDataWrapper<ProfessionalInfoLookupResponse>>()
    var saveProfessionalInfoApi = SingleLiveEvent<LiveDataWrapper<SaveProfessionalInfoResponse>>()

    var bankInfoLookUpsApi = SingleLiveEvent<LiveDataWrapper<BankInfoLookupsResponse>>()
    var accountTitleByIBANApi = SingleLiveEvent<LiveDataWrapper<AccountTitleByIBANResponse>>()
    var allBranchesResponse = SingleLiveEvent<LiveDataWrapper<BranchResponse>>()
    var saveBankInfoApi = SingleLiveEvent<LiveDataWrapper<SaveBankInfoResponse>>()

    var getVideoCallUrlApi = SingleLiveEvent<LiveDataWrapper<GetVideoCallUrlResponse>>()
    var saveVideoCallSlotApi = SingleLiveEvent<LiveDataWrapper<SaveVideoCallSlotResponse>>()

    var checkCnicApi = SingleLiveEvent<LiveDataWrapper<DuplicationCheckResponse>>()
    var checkEmailOrMobileApi = SingleLiveEvent<LiveDataWrapper<DuplicationCheckResponse>>()



    val loadingLiveData = SingleLiveEvent<Boolean>()
    private val job = SupervisorJob()
    val mainUiScope = CoroutineScope(mainDispatcher + job)
    val mainIoScope = CoroutineScope(ioDispatcher + job)
    val gson = Gson()

    fun getPersonalInfoLookups() {
        mainUiScope.launch {

            personalInfoLookUpsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getPersonalInfoLookup()
                }.await()

                try {
                    personalInfoLookUpsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    personalInfoLookUpsApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                personalInfoLookUpsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun savePersonalInfo(savePersonalInfoDTO: SavePersonalInfoDTO) {
        mainUiScope.launch {

            savePersonalInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.savePersonalInfo(savePersonalInfoDTO)
                }.await()

                try {
                    savePersonalInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    savePersonalInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                savePersonalInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun changeAccountType(changeAccountTypeDTO: ChangeAccountTypeDTO) {
        mainUiScope.launch {

            changeAccountTypeApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.changeAccountType(changeAccountTypeDTO)
                }.await()

                try {
                    changeAccountTypeApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    changeAccountTypeApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                changeAccountTypeApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getAccountTypes(){
        mainUiScope.launch {

            accountTypesApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getAccountTypes()
                }.await()

                try {
                    accountTypesApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    accountTypesApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                accountTypesApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getProfessionalInfoLookups(token: String) {
        mainUiScope.launch {

            professionalInfoLookUpsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getProfessionalInfoLookup(token = token)
                }.await()

                try {
                    professionalInfoLookUpsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    professionalInfoLookUpsApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                professionalInfoLookUpsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun resumeProfessionalInfo(token: String, customerId: String) {
        mainUiScope.launch {

            resumeProfessionalInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.resumeProfessionalInfo(
                        token = token,
                        customerID = customerId
                    )
                }.await()

                try {
                    resumeProfessionalInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    resumeProfessionalInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                resumeProfessionalInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun resumeBankInfo(token: String, customerId: String) {
        mainUiScope.launch {

            resumeBankInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.resumeBankInfo(token = token, customerID = customerId)
                }.await()

                try {
                    resumeBankInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    resumeBankInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                resumeBankInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveProfessionalInfo(token: String, saveProfessionalInfoDTO: SaveProfessionalInfoDTO) {
        mainUiScope.launch {

            saveProfessionalInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.saveProfessionalInfo(
                        token,
                        saveProfessionalInfoRequestBody(saveProfessionalInfoDTO)
                    )
                }.await()

                try {
                    saveProfessionalInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveProfessionalInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveProfessionalInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getBankInfoLookups(token: String) {
        mainUiScope.launch {

            bankInfoLookUpsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getBankInfoLookup(token = token)
                }.await()

                try {
                    bankInfoLookUpsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    bankInfoLookUpsApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                bankInfoLookUpsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getAccountTitleByIBAN(token: String, accountTitleDTO: GetAccountTitleDTO) {
        mainUiScope.launch {
            accountTitleByIBANApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)
            try {
                val data = mainIoScope.async {
                    val request = gson.toJson(accountTitleDTO)
                    return@async useCase.getAccountTitleByIBAN(
                        token,
                        request.toRequestBody("application/json".toMediaTypeOrNull())
                    )
                }.await()
                try {
                    accountTitleByIBANApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                }
                setLoadingVisibility(false)
            } catch (e: Exception) {
                setLoadingVisibility(false)
                accountTitleByIBANApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getBranchesData(token: String, branchID: Int) {
        mainUiScope.launch {
            allBranchesResponse.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)
            try {
                val data = mainIoScope.async {
                    return@async useCase.getBranches(token, branchID)
                }.await()
                try {
                    allBranchesResponse.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                }
                setLoadingVisibility(false)
            } catch (e: Exception) {
                setLoadingVisibility(false)
                allBranchesResponse.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveBankInfo(token: String, saveBankInfoDTO: SaveBankInfoDTO) {
        mainUiScope.launch {

            saveBankInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    val request = gson.toJson(saveBankInfoDTO)
                        .toRequestBody("application/json".toMediaTypeOrNull())
                    return@async useCase.saveBankInfo(token, request)
                }.await()

                try {
                    saveBankInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveBankInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveBankInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun scanFrontIdCard(body: MultipartBody.Part) {
        mainUiScope.launch {

            scanFrontIdCardApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.scanFrontIdCard(body)
                }.await()

                try {
                    scanFrontIdCardApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    scanFrontIdCardApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                scanFrontIdCardApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun scanBackIdCard(body: MultipartBody.Part) {
        mainUiScope.launch {

            scanBackIdCardApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.scanBackIdCard(body)
                }.await()

                try {
                    scanBackIdCardApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    scanBackIdCardApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                scanBackIdCardApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun resendOTP(token: String, sendOtpDTO: SendOtpDTO) {
        mainUiScope.launch {

            resendOTPApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    val request = gson.toJson(sendOtpDTO)
                    return@async useCase.resendOTP(
                        token = token,
                        requestBody = request.toRequestBody("application/json".toMediaTypeOrNull())
                    )
                }.await()

                try {
                    resendOTPApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    resendOTPApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                resendOTPApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun verifyOTP(token: String, verifyOtpDTO: VerifyOtpDTO) {
        mainUiScope.launch {

            verifyOTPApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    val request = gson.toJson(verifyOtpDTO)
                    return@async useCase.verifyOTP(
                        token = token,
                        requestBody = request.toRequestBody("application/json".toMediaTypeOrNull())
                    )
                }.await()

                setLoadingVisibility(false)

                try {
                    verifyOTPApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    verifyOTPApi.value = LiveDataWrapper.error(e)
                }

            } catch (e: Exception) {
                setLoadingVisibility(false)
                verifyOTPApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun resumePersonalInfo(token: String, customerId: String) {
        mainUiScope.launch {

            resumePersonalInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.resumePersonalInfo(token, customerId)
                }.await()

                try {
                    resumePersonalInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    resumePersonalInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                resumePersonalInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }


    fun getVideoCallUrl(token: String, customerId: String) {
        mainUiScope.launch {

            getVideoCallUrlApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getVideocallURL(token = token, customerID = customerId)
                }.await()

                try {
                    getVideoCallUrlApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getVideoCallUrlApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getVideoCallUrlApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveVideoCallSlot(token: String, saveVideoCallSlotDTO: SaveVideoCallSlotDTO) {
        mainUiScope.launch {

            saveVideoCallSlotApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.saveVideoCallSlot(token, saveVideoCallSlotDTO)
                }.await()

                try {
                    saveVideoCallSlotApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveVideoCallSlotApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveVideoCallSlotApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun checkCustomerCnic(cnicCheckDTO: CNICCheckDTO) {
        mainUiScope.launch {

            checkCnicApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.customerCnicCheck(cnicCheckDTO)
                }.await()

                try {
                    checkCnicApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    checkCnicApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                checkCnicApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun checkCustomerMobileOrEmail(mobileCheckDTO: MobileCheckDTO) {
        mainUiScope.launch {

            checkEmailOrMobileApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.customerMobileOrEmailCheck(mobileCheckDTO)
                }.await()

                try {
                    checkEmailOrMobileApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    checkEmailOrMobileApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                checkEmailOrMobileApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    private fun setLoadingVisibility(visible: Boolean) {
        loadingLiveData.postValue(visible)
    }

    private fun saveProfessionalInfoRequestBody(saveProfessionalInfoDTO: SaveProfessionalInfoDTO): RequestBody {
        val request = gson.toJson(saveProfessionalInfoDTO)
        return request.toRequestBody("application/json".toMediaTypeOrNull())
    }

    override fun onCleared() {
        super.onCleared()
        this.job.cancel()
    }
}