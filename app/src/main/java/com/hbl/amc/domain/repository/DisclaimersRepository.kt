package com.hbl.amc.domain.repository

import com.hbl.amc.api_service.ApiService
import com.hbl.amc.domain.RequestDTO.SampleDownloadDTO
import com.hbl.amc.domain.model.DocumentChecklistResponse
import com.hbl.amc.domain.model.SampleDownloadResponse
import com.hbl.amc.domain.model.UploadDocumentResponse
import okhttp3.MultipartBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DisclaimersRepository : KoinComponent {

    private val apiService: ApiService by inject()

    suspend fun getCustomerDocumentsChecklist(customerID: String): DocumentChecklistResponse {
        return apiService.getCustomerDocumentsChecklist(customerId = customerID)
    }

    suspend fun uploadDocument(
        docFile: MultipartBody.Part,
        docId: MultipartBody.Part,
        customerID: MultipartBody.Part?
    ): UploadDocumentResponse {
        return apiService.uploadDocument(
            docFile = docFile,
            documentTypeId = docId,
            customerId = customerID
        )
    }

    suspend fun getSampleDocument(/*token: String, */docId: Int): SampleDownloadResponse {
        return apiService.getSampleDocument(SampleDownloadDTO( docId))
    }
}