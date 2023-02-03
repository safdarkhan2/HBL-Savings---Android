package com.hbl.amc.domain.repository

import com.hbl.amc.api_service.ApiService
import com.hbl.amc.domain.RequestDTO.SaveFatcaInfoDTO
import com.hbl.amc.domain.model.*
import com.hbl.amc.ui.SECTION_ID
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.http.Header
import retrofit2.http.Part
import java.io.File

class RegulatoryInfoRepository : KoinComponent {

    private val apiService: ApiService by inject()

    suspend fun crsInfoLookups(token: String): CRSInfoLookupsResponse {
        return apiService.getCRSInfoLookups(token = token)
    }

    suspend fun saveCrsInfo(token: String, body: RequestBody): SaveCRSInfoResponse {
        return apiService.saveCRSInfo(token = token, requestBody = body)
    }

    suspend fun rpqInfoLookups(token: String): RpqInfoLookupsResponse {
        return apiService.getRPQInfoLookups(token)
    }

    suspend fun saveRpqInfo(token: String, body: RequestBody): SaveRPQInfoResponse {
        return apiService.saveRPQInfo(token = token, requestBody = body)
    }

    suspend fun kycInfoLookups(token : String): KYCLookupsResponse {
        return apiService.getKYCInfoLookups(token = token)
    }

    suspend fun saveKYCInfo(token: String, body: RequestBody): SaveKYCInfoResponse {
        return apiService.saveKYCInfo(token = token, requestBody = body)
    }

    suspend fun fatcaInfoLookups(token: String): FatcaInfoLookupsResponse {
        return apiService.getFatcaInfoLookups(token = token)
    }

    suspend fun saveFatcaInfo(token: String, body: SaveFatcaInfoDTO): SaveFatcaInfoResponse {
        return apiService.saveFatcaInfo(token = token, requestBody = body)
    }

    suspend fun getDisclaimerInfo(token: String): GetDisclaimerResponse {
        return apiService.getDisclaimersInfoLookups(token = token, sectionId = SECTION_ID)
    }

    suspend fun saveDisclaimerInfo(token: String, body: RequestBody): SaveDisclaimerResponse {
        return apiService.saveDisclaimerInfo(token = token, requestBody = body)
    }

    suspend fun resumeKYC(token: String, customerID: String): ResumeKYCResponse {
        return apiService.resumeKYC(token = token, customerId = customerID)
    }

    suspend fun resumeRPQ(token: String, customerID: String): ResumeRPQResponse {
        return apiService.resumeRPQ(token = token, customerId = customerID)
    }

    suspend fun resumeFatca(token: String, customerId: String): ResumeFatcaResponse {
        return apiService.resumeFatca(token = token, customerId = customerId)
    }

    suspend fun resumeCRS(token: String, customerId: String): ResumeCRSResponse {
        return apiService.resumeCRS(token = token, customerId = customerId)
    }

    suspend fun saveNextOfKin(
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
    ): SaveNextOfKinResponse {
        return apiService.addNextOfKin(
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
    }

    suspend fun updateNextOfKin(
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
    ): SaveNextOfKinResponse {
        return apiService.updateNextOfKin(
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
    }

    suspend fun deleteNextOfKin(token: String,id : String) : DeleteNextOfKin {
        return apiService.deleteNExtOfKin(token = token, id = id)
    }
}