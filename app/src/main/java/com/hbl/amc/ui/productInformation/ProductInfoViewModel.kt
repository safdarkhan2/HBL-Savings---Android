package com.hbl.amc.ui.productInformation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hbl.amc.domain.RequestDTO.SaveOtherInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.usecase.ProductInfoUseCase
import com.hbl.amc.utils.SingleLiveEvent
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.component.KoinComponent

class ProductInfoViewModel(
    mainDispatcher: CoroutineDispatcher,
    ioDispatcher: CoroutineDispatcher, private val useCase: ProductInfoUseCase
) : ViewModel(), KoinComponent {

    var getOtherInfoApi = SingleLiveEvent<LiveDataWrapper<SaveOtherInfoResponse>>()
    var saveOtherInfoApi = SingleLiveEvent<LiveDataWrapper<SaveOtherInfoResponse>>()
    var getProductsApi = SingleLiveEvent<LiveDataWrapper<ProductsGetResponse>>()
    var saveProductsApi = SingleLiveEvent<LiveDataWrapper<ProductSaveResponse>>()
    var getMutualFundsApi = SingleLiveEvent<LiveDataWrapper<MutualFundsGetResponse>>()
    var getVPSFundsDataApi = SingleLiveEvent<LiveDataWrapper<VPSFundsDataGetResponse>>()
    var getVPSFundsApi = SingleLiveEvent<LiveDataWrapper<VPSFundsGetResponse>>()
    var saveMutualFundApi = SingleLiveEvent<LiveDataWrapper<MutualFundSaveResponse>>()
    var saveVPSFundApi = SingleLiveEvent<LiveDataWrapper<VPSFundSaveResponse>>()
    var getSelectedMutualFundsApi = SingleLiveEvent<LiveDataWrapper<GetSelectedFundsResponse>>()
    var getSelectedVPSFundsApi = SingleLiveEvent<LiveDataWrapper<GetSelectedFundsResponse>>()
    var getRiskLevelsApi = SingleLiveEvent<LiveDataWrapper<RiskLevelsResponse>>()

    val loadingLiveData = MutableLiveData<Boolean>()

    private val job = SupervisorJob()
    val mainUiScope = CoroutineScope(mainDispatcher + job)
    val mainIoScope = CoroutineScope(ioDispatcher + job)
    private val gson = Gson()

    fun getProducts(token: String, customerId: String) {
        mainUiScope.launch {
            getProductsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getProducts(token = token, customerId = customerId)
                }.await()

                setLoadingVisibility(false)
                try {
                    getProductsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    getProductsApi.value = LiveDataWrapper.error(e)
                }

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getProductsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveProduct(token: String, productSaveDTO: ProductSaveResult) {
        mainUiScope.launch {
            saveProductsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    val request = gson.toJson(productSaveDTO)
                        .toRequestBody("application/json".toMediaTypeOrNull())
                    return@async useCase.saveProduct(token, request)
                }.await()

                setLoadingVisibility(false)
                try {
                    saveProductsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    saveProductsApi.value = LiveDataWrapper.error(e)
                }

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getProductsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getMutualFunds(token: String, customerId: String) {
        mainUiScope.launch {

            getMutualFundsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getMutualFunds(token = token, customerId = customerId)
                }.await()

                try {
                    getMutualFundsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getMutualFundsApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getMutualFundsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getVPSFundsData(token: String, customerId: String) {
        mainUiScope.launch {

            getVPSFundsDataApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getVPSFundsData(token = token, customerId = customerId)
                }.await()

                try {
                    getVPSFundsDataApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getVPSFundsDataApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getVPSFundsDataApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getVPSFunds(token: String, categoryId: String) {
        mainUiScope.launch {

            getVPSFundsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getVPSFunds(token = token, categoryId = categoryId)
                }.await()

                try {
                    getVPSFundsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getVPSFundsApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getVPSFundsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getSelectedMutualFunds(token: String, customerId: String) {
        mainUiScope.launch {

            getSelectedMutualFundsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getSelectedMutualFunds(
                        token = token,
                        customerId = customerId
                    )
                }.await()

                try {
                    getSelectedMutualFundsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getSelectedMutualFundsApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getSelectedMutualFundsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getSelectedVPSFunds(token: String, customerId: String) {
        mainUiScope.launch {

            getSelectedVPSFundsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getSelectedVPSFunds(token = token, customerId = customerId)
                }.await()

                try {
                    getSelectedVPSFundsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    getSelectedVPSFundsApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getSelectedVPSFundsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveMutualFund(token: String, saveMutualFundDTO: MutualFundSaveResult) {
        mainUiScope.launch {
            saveMutualFundApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    val request = gson.toJson(saveMutualFundDTO)
                        .toRequestBody("application/json".toMediaTypeOrNull())
                    return@async useCase.saveMutualFund(token, request)
                }.await()

                setLoadingVisibility(false)
                try {
                    saveMutualFundApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    saveMutualFundApi.value = LiveDataWrapper.error(e)
                }

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveMutualFundApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveVPSFund(token: String, saveVpsFundDTO: VPSFundSaveResult) {
        mainUiScope.launch {
            saveVPSFundApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    val request = gson.toJson(saveVpsFundDTO)
                        .toRequestBody("application/json".toMediaTypeOrNull())
                    return@async useCase.saveVPSFund(token, request)
                }.await()

                setLoadingVisibility(false)
                try {
                    saveVPSFundApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    saveVPSFundApi.value = LiveDataWrapper.error(e)
                }

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveVPSFundApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getOtherInfo(token: String, customerId: String) {
        mainUiScope.launch {
            getOtherInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getOtherInfo(token = token, customerId = customerId)
                }.await()

                setLoadingVisibility(false)
                try {
                    getOtherInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    getOtherInfoApi.value = LiveDataWrapper.error(e)
                }

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getOtherInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getRiskLevels(token: String) {
        mainUiScope.launch {
            getRiskLevelsApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.getRiskLevels(token = token)
                }.await()

                try {
                    getRiskLevelsApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    getRiskLevelsApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                getRiskLevelsApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun saveOtherInfo(token: String, saveOtherInfoDTO: SaveOtherInfoDTO) {
        mainUiScope.launch {

            saveOtherInfoApi.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)

            try {
                val data = mainIoScope.async {
                    return@async useCase.saveOtherInfo(
                        token,
                        saveOtherInfoRequestBody(saveOtherInfoDTO)
                    )
                }.await()

                try {
                    saveOtherInfoApi.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    setLoadingVisibility(false)
                    saveOtherInfoApi.value = LiveDataWrapper.error(e)
                }

                setLoadingVisibility(false)

            } catch (e: Exception) {
                setLoadingVisibility(false)
                saveOtherInfoApi.value = LiveDataWrapper.error(e)
            }
        }
    }

    private fun saveOtherInfoRequestBody(saveOtherInfoDTO: SaveOtherInfoDTO): RequestBody {
        val request = gson.toJson(saveOtherInfoDTO)
        return request.toRequestBody("application/json".toMediaTypeOrNull())
    }

    private fun setLoadingVisibility(visible: Boolean) {
        loadingLiveData.postValue(visible)
    }

    override fun onCleared() {
        super.onCleared()
        this.job.cancel()
    }
}