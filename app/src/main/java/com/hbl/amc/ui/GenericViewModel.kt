package com.hbl.amc.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.AllStepsResponse
import com.hbl.amc.domain.model.CityResponse
import com.hbl.amc.domain.model.CountryResponse
import com.hbl.amc.domain.model.DocumentsTypesResponse
import com.hbl.amc.domain.usecase.GenericUseCase
import com.hbl.amc.utils.SingleLiveEvent
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent

class GenericViewModel(
    mainDispatcher: CoroutineDispatcher,
    ioDispatcher: CoroutineDispatcher, private val useCase: GenericUseCase
) : ViewModel(), KoinComponent {
    var allCountriesResponse = SingleLiveEvent<LiveDataWrapper<CountryResponse>>()
    var allCitiesResponse = SingleLiveEvent<LiveDataWrapper<CityResponse>>()
    var documentTypesResponse = SingleLiveEvent<LiveDataWrapper<DocumentsTypesResponse>>()
    var onboardingStepsResponse = SingleLiveEvent<LiveDataWrapper<AllStepsResponse>>()

    val loadingLiveData = MutableLiveData<Boolean>()
    private val job = SupervisorJob()
    private val mUiScope = CoroutineScope(mainDispatcher + job)
    private val mIoScope = CoroutineScope(ioDispatcher + job)

    init {
        //call reset to reset all VM data as required
        resetValues()
    }

    //Reset any member as per flow
    fun resetValues() {

    }

    //can be called from View explicitly if required
    //Keep default scope
//    fun requestProductData(param:String) {
    fun getCountriesData() {
        mUiScope.launch {
            allCountriesResponse.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)
            try {
                val data = mIoScope.async {
                    return@async useCase.getCountries()
//                        return@async useCase.getCountries(param)
                }.await()
                try {
                    allCountriesResponse.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                }
                setLoadingVisibility(false)
            } catch (e: Exception) {
                setLoadingVisibility(false)
                allCountriesResponse.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getCitiesData(countryID: Int) {
        mUiScope.launch {
            allCitiesResponse.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)
            try {
                val data = mIoScope.async {
                    return@async useCase.getCities(countryID)
//                        return@async useCase.getCountries(param)
                }.await()
                try {
                    allCitiesResponse.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                }
                setLoadingVisibility(false)
            } catch (e: Exception) {
                setLoadingVisibility(false)
                allCitiesResponse.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getAllDocumentsTypes() {
        mUiScope.launch {
            documentTypesResponse.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)
            try {
                val data = mIoScope.async {
                    return@async useCase.getDocumentsTypes()
                }.await()
                try {
                    documentTypesResponse.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    documentTypesResponse.value = LiveDataWrapper.error(e)
                }
                setLoadingVisibility(false)
            } catch (e: Exception) {
                setLoadingVisibility(false)
                documentTypesResponse.value = LiveDataWrapper.error(e)
            }
        }
    }

    fun getOnboardingSteps() {
        mUiScope.launch {
            onboardingStepsResponse.value = LiveDataWrapper.loading()
            setLoadingVisibility(true)
            try {
                val data = mIoScope.async {
                    return@async useCase.getOnboardingSteps()
                }.await()
                try {
                    onboardingStepsResponse.value = LiveDataWrapper.success(data)
                } catch (e: Exception) {
                    onboardingStepsResponse.value = LiveDataWrapper.error(e)
                }
                setLoadingVisibility(false)
            } catch (e: Exception) {
                setLoadingVisibility(false)
                onboardingStepsResponse.value = LiveDataWrapper.error(e)
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