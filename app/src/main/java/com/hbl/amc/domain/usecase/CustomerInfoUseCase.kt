package com.hbl.amc.domain.usecase

import com.hbl.amc.domain.RequestDTO.*
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.repository.CustomerInfoRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class CustomerInfoUseCase : KoinComponent {

    private val customerInfoRepository by inject<CustomerInfoRepository>()

    suspend fun getPersonalInfoLookup(): PersonalInfoLookupsResponse {
        return customerInfoRepository.PersonalInfoLookups()
    }

    suspend fun savePersonalInfo(savePersonalInfoDTO: SavePersonalInfoDTO) : SavePersonalInfoResponse {
        return customerInfoRepository.savePersonalInfo(savePersonalInfoDTO)
    }

    suspend fun getProfessionalInfoLookup(token: String): ProfessionalInfoLookupResponse {
        return customerInfoRepository.professionalInfoLookups(token = token)
    }

    suspend fun saveProfessionalInfo(token : String, body: RequestBody) : SaveProfessionalInfoResponse {
        return customerInfoRepository.saveProfessionalInfo(token = token, body = body)
    }

    suspend fun getBankInfoLookup(token: String): BankInfoLookupsResponse {
        return customerInfoRepository.BankInfoLookups(token = token)
    }

    suspend fun getAccountTitleByIBAN(token : String, requestBody: RequestBody) : AccountTitleByIBANResponse {
        return customerInfoRepository.getAccountTitleByIBAN(token, requestBody)
    }

    suspend fun getBranches(token : String, branchID : Int): BranchResponse {
        return customerInfoRepository.getBranches(token, branchID)
    }

    suspend fun saveBankInfo(token : String, body: RequestBody) : SaveBankInfoResponse {
        return customerInfoRepository.saveBankInfo(token = token, body = body)
    }

    suspend fun scanFrontIdCard(body: MultipartBody.Part) : CnicUploadResponse {
        return customerInfoRepository.scanFrontIdCard(body)
    }

    suspend fun scanBackIdCard(body: MultipartBody.Part) : CnicUploadResponse {
        return customerInfoRepository.scanBackIdCard(body)
    }

    suspend fun resumeProfessionalInfo(token : String, customerID : String) : ResumeProfessionalInfoRes {
        return customerInfoRepository.resumeProfessionalInfo(token = token, customerID = customerID)
    }

    suspend fun resumeBankInfo(token : String, customerID : String) : ResumeBankInfoRes {
        return customerInfoRepository.resumeBankInfo(token = token, customerID = customerID)
    }

    suspend fun resendOTP(token : String, requestBody: RequestBody) : SendOTPResponse {
        return customerInfoRepository.resendOTP(token = token, requestBody = requestBody)
    }

    suspend fun verifyOTP(token : String, requestBody: RequestBody) : VerifyOTPResponse {
        return customerInfoRepository.verifyOTP(token = token, requestBody = requestBody)
    }

    suspend fun resumePersonalInfo(token: String, customerID: String) : ResumePersonalInfo {
        return customerInfoRepository.resumePersonalInfo(token = token, customerID = customerID)
    }

    suspend fun changeAccountType(changeAccountTypeDTO: ChangeAccountTypeDTO): SavePersonalInfoResponse {
        return customerInfoRepository.changeAccountType(changeAccountTypeDTO)
    }

    suspend fun getAccountTypes() : AccountTypesResponse {
        return customerInfoRepository.getAccountTypes()
    }

    suspend fun getVideocallURL(token: String, customerID: String): GetVideoCallUrlResponse {
        return customerInfoRepository.getVideocallURL(token = token, customerID = customerID)
    }

    suspend fun saveVideoCallSlot(token: String, saveVideoCallSlotDTO: SaveVideoCallSlotDTO): SaveVideoCallSlotResponse {
        return customerInfoRepository.saveVideoCallSlot(token = token, saveVideoCallSlotDTO = saveVideoCallSlotDTO)
    }

    suspend fun customerCnicCheck(
        requestBody: CNICCheckDTO
    ): DuplicationCheckResponse {
        return customerInfoRepository.customerCnicCheck(requestBody)
    }

    suspend fun customerMobileOrEmailCheck(
        requestBody: MobileCheckDTO
    ): DuplicationCheckResponse {
        return customerInfoRepository.customerMobileOrEmailCheck(requestBody)
    }
}