package com.hbl.amc.domain.usecase

import com.hbl.amc.domain.model.AllStepsResponse
import com.hbl.amc.domain.model.CityResponse
import com.hbl.amc.domain.model.CountryResponse
import com.hbl.amc.domain.model.DocumentsTypesResponse
import com.hbl.amc.domain.repository.GenericRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GenericUseCase : KoinComponent {

    private val genericRepository by inject<GenericRepository>()

    suspend fun getCountries(): CountryResponse {
        return genericRepository.getCountries()
    }

    suspend fun getCities(countryID : Int): CityResponse {
        return genericRepository.getCities(countryID)
    }

    suspend fun getDocumentsTypes() : DocumentsTypesResponse {
        return genericRepository.getDocumentsTypes()
    }

    suspend fun getOnboardingSteps() : AllStepsResponse {
        return genericRepository.getOnboardingSteps()
    }
}