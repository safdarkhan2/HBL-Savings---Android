package com.hbl.amc.domain.repository

import com.hbl.amc.api_service.ApiService
import com.hbl.amc.domain.model.GetPreviewInfoResponse
import com.hbl.amc.domain.model.ResumeRPQResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PreviewRepository : KoinComponent {

    private val apiService: ApiService by inject()

    suspend fun getPreviewInfo(customerID : String) : GetPreviewInfoResponse {
        return apiService.getPreviewInfo(customerId = customerID)
    }
}
