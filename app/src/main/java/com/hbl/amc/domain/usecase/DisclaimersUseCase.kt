package com.hbl.amc.domain.usecase

import com.hbl.amc.domain.model.DocumentChecklistResponse
import com.hbl.amc.domain.model.SampleDownloadResponse
import com.hbl.amc.domain.model.UploadDocumentResponse
import com.hbl.amc.domain.repository.DisclaimersRepository
import okhttp3.MultipartBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DisclaimersUseCase : KoinComponent {

    val disclaimersRepository by inject<DisclaimersRepository>()

    suspend fun getCustomerDocumentChecklist(customerID : String) : DocumentChecklistResponse {
        return disclaimersRepository.getCustomerDocumentsChecklist(customerID)
    }

    suspend fun uploadDocument(
        docFile: MultipartBody.Part,
        docId: MultipartBody.Part,
        customerID: MultipartBody.Part?
    ): UploadDocumentResponse {
        return disclaimersRepository.uploadDocument(
            docFile = docFile,
            docId = docId,
            customerID = customerID
        )
    }

    suspend fun getSampleDocument(/*token: String, */docId: Int): SampleDownloadResponse {
        return disclaimersRepository.getSampleDocument(/*token, */docId)
    }
}