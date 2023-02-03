package com.hbl.amc.domain.repository

import com.hbl.amc.api_service.ApiService
import com.hbl.amc.domain.RequestDTO.*
import com.hbl.amc.domain.model.*
import com.hbl.amc.ui.API_ID
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.File

class CustomerInfoRepository : KoinComponent {

    private val apiService: ApiService by inject()

    /**
     * Request data from backend
     */
    suspend fun PersonalInfoLookups(): PersonalInfoLookupsResponse {
        return apiService.getPersonalInfoLookups()
    }

    suspend fun savePersonalInfo(savePersonalInfoDTO: SavePersonalInfoDTO): SavePersonalInfoResponse {
        return apiService.savePersonalInfo(savePersonalInfoDTO)
    }

    suspend fun professionalInfoLookups(token: String): ProfessionalInfoLookupResponse {
        return apiService.getProfessionalInfoLookups(token = token)
    }

    suspend fun saveProfessionalInfo(
        token: String,
        body: RequestBody
    ): SaveProfessionalInfoResponse {
        return apiService.saveProfessionalInfo(token = token, requestBody = body)
    }

    suspend fun BankInfoLookups(token: String): BankInfoLookupsResponse {
        return apiService.getBankInfoLookups(token = token)
    }

    suspend fun getAccountTitleByIBAN(
        token: String,
        requestBody: RequestBody
    ): AccountTitleByIBANResponse {
        return apiService.getAccountTitleByIBAN(token, requestBody)
    }

    suspend fun getBranches(token: String, branchID: Int): BranchResponse {
        return apiService.getBranches(token, branchID)
    }

    suspend fun saveBankInfo(token: String, body: RequestBody): SaveBankInfoResponse {
        return apiService.saveBankInfo(token = token, requestBody = body)
    }

    suspend fun scanFrontIdCard(body: MultipartBody.Part): CnicUploadResponse {
        return apiService.uploadFrontCnic(body)
    }

    suspend fun scanBackIdCard(body: MultipartBody.Part): CnicUploadResponse {
        return apiService.uploadBackCnic(body)
    }

    suspend fun resumeProfessionalInfo(
        token: String,
        customerID: String
    ): ResumeProfessionalInfoRes {
        return apiService.resumeProfessionalInfo(token = token, customerId = customerID)
    }

    suspend fun resumeBankInfo(token: String, customerID: String): ResumeBankInfoRes {
        return apiService.resumeBankInfo(token = token, customerId = customerID)
    }

    suspend fun resendOTP(token: String, requestBody: RequestBody): SendOTPResponse {
        return apiService.resendOTP(token = token, requestBody = requestBody)
    }

    suspend fun verifyOTP(token: String, requestBody: RequestBody): VerifyOTPResponse {
        return apiService.verifyOTP(token = token, requestBody = requestBody)
    }

    suspend fun resumePersonalInfo(token: String, customerID: String) : ResumePersonalInfo {
        return apiService.resumePersonalInfo(token = token, customerId = customerID)
    }

    suspend fun changeAccountType(changeAccountTypeDTO: ChangeAccountTypeDTO): SavePersonalInfoResponse {
        return apiService.changeAccountType(changeAccountTypeDTO)
    }

    suspend fun getAccountTypes() : AccountTypesResponse {
        return apiService.getAccountTypes()
    }

    suspend fun getVideocallURL(token: String, customerID: String): GetVideoCallUrlResponse {
        return apiService.getVideocallURL(token = token, customerId = customerID)
    }

    suspend fun saveVideoCallSlot(
        token: String,
        saveVideoCallSlotDTO: SaveVideoCallSlotDTO
    ): SaveVideoCallSlotResponse {
        return apiService.saveVideoCallSlot(token = token, requestBody = saveVideoCallSlotDTO)
    }

    suspend fun customerCnicCheck(
        requestBody: CNICCheckDTO
    ): DuplicationCheckResponse {
        return apiService.customerCnicCheck(requestBody)
    }

    suspend fun customerMobileOrEmailCheck(
        requestBody: MobileCheckDTO
    ): DuplicationCheckResponse {
        return apiService.customerMobileOrEmailCheck(requestBody)
    }
}