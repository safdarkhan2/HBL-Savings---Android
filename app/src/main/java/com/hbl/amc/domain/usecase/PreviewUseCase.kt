package com.hbl.amc.domain.usecase

import com.hbl.amc.domain.model.GetPreviewInfoResponse
import com.hbl.amc.domain.repository.PreviewRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PreviewUseCase : KoinComponent {

    private val previewRepository by inject<PreviewRepository>()

    suspend fun getPreviewInfo(customerID: String): GetPreviewInfoResponse {
        return previewRepository.getPreviewInfo(customerID = customerID)
    }
}