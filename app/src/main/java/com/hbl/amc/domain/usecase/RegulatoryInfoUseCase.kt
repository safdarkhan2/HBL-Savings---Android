package com.hbl.amc.domain.usecase

import com.hbl.amc.domain.RequestDTO.SaveFatcaInfoDTO
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.repository.RegulatoryInfoRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.http.Header
import java.io.File

class RegulatoryInfoUseCase : KoinComponent {

    private val regulatoryInfoRepository by inject<RegulatoryInfoRepository>()

    suspend fun getCrsInfoLookup(token: String): CRSInfoLookupsResponse {
        return regulatoryInfoRepository.crsInfoLookups(token = token)
    }

    suspend fun saveCrsInfo(token: String, body: RequestBody): SaveCRSInfoResponse {
        return regulatoryInfoRepository.saveCrsInfo(token = token, body = body)
    }

    suspend fun getRpqInfoLookup(token: String): RpqInfoLookupsResponse {
        return regulatoryInfoRepository.rpqInfoLookups(token = token)
    }

    suspend fun saveRpqInfo(token: String, body: RequestBody): SaveRPQInfoResponse {
        return regulatoryInfoRepository.saveRpqInfo(token = token, body = body)
    }

    suspend fun getKYCInfoLookup(token: String): KYCLookupsResponse {
        return regulatoryInfoRepository.kycInfoLookups(token = token)
    }

    suspend fun saveKYCInfo(token: String, body: RequestBody): SaveKYCInfoResponse {
        return regulatoryInfoRepository.saveKYCInfo(token = token, body = body)
    }

    suspend fun getFatcaInfoLookup(token: String): FatcaInfoLookupsResponse {
        return regulatoryInfoRepository.fatcaInfoLookups(token = token)
    }

    suspend fun saveFatcaInfo(token: String, saveFatcaInfoDTO: SaveFatcaInfoDTO): SaveFatcaInfoResponse {
        return regulatoryInfoRepository.saveFatcaInfo(token = token, body = saveFatcaInfoDTO)
    }

    suspend fun getDisclaimerInfoLookup(token : String): GetDisclaimerResponse {
        return regulatoryInfoRepository.getDisclaimerInfo(token = token)
    }

    suspend fun saveDisclaimerInfo(token: String, body: RequestBody): SaveDisclaimerResponse {
        return regulatoryInfoRepository.saveDisclaimerInfo(token = token, body = body)
    }

    suspend fun resumeKYC(token: String, customerID: String): ResumeKYCResponse {
        return regulatoryInfoRepository.resumeKYC(token = token, customerID = customerID)
    }

    suspend fun resumeRPQ(token: String, customerID: String): ResumeRPQResponse {
        return regulatoryInfoRepository.resumeRPQ(token = token, customerID = customerID)
    }

    suspend fun resumeFatca(token: String, customerId: String): ResumeFatcaResponse {
        return regulatoryInfoRepository.resumeFatca(token = token, customerId = customerId)
    }

    suspend fun resumeCRS(token: String, customerId: String): ResumeCRSResponse {
        return regulatoryInfoRepository.resumeCRS(token = token, customerId = customerId)
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
        return regulatoryInfoRepository.saveNextOfKin(
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
        return regulatoryInfoRepository.updateNextOfKin(
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
        return regulatoryInfoRepository.deleteNextOfKin(token = token, id = id)
    }
}