package com.hbl.amc.domain.repository

import com.hbl.amc.api_service.ApiService
import com.hbl.amc.domain.model.AllStepsResponse
import com.hbl.amc.domain.model.CityResponse
import com.hbl.amc.domain.model.CountryResponse
import com.hbl.amc.domain.model.DocumentsTypesResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GenericRepository : KoinComponent {

    private val apiService: ApiService by inject()

    /**
     * Request data from backend
     */
    suspend fun getCountries(): CountryResponse {
        return apiService.getCountries()
    }

    suspend fun getCities(countryID : Int) : CityResponse {
        return apiService.getCities(countryID)
    }

    suspend fun getDocumentsTypes() : DocumentsTypesResponse {
        return apiService.getDocumentTypes()
    }

    suspend fun getOnboardingSteps() : AllStepsResponse {
        return apiService.getAllOnboardingSteps()
    }
}