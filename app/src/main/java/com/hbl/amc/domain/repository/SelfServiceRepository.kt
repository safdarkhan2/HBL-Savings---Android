package com.hbl.amc.domain.repository

import com.hbl.amc.api_service.ApiService
import com.hbl.amc.domain.RequestDTO.SavePledgeInfoDTO
import com.hbl.amc.domain.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SelfServiceRepository : KoinComponent {

    private val apiService: ApiService by inject()

    suspend fun getInvestmentInfoLookups(token:String,customerId: String): GetInvestmentInfoLookupResponse {
        return apiService.getInvestmentInfoLookup(token = token, customerId = customerId)
    }

    suspend fun saveInvestmentInfo(
        token: String,
        customerId: MultipartBody.Part,
        transactionTypeId: MultipartBody.Part,
        amount: MultipartBody.Part,
        paymentTypeId: MultipartBody.Part,
        fundId: MultipartBody.Part,
        unitTypeId: MultipartBody.Part,
        incomeTypeId: MultipartBody.Part,
        incomeSpecifyId: MultipartBody.Part,
        incomePaymentFrequency: MultipartBody.Part,
        paymentReference: MultipartBody.Part,
        isAccepted: MultipartBody.Part,
        billNumber: MultipartBody.Part?,
        fundBankAccountNumber: MultipartBody.Part?,
        IBFTRecepit: MultipartBody.Part?,
        paymentDate: MultipartBody.Part?,
        niftStatus: MultipartBody.Part?,
        IBFTReferenceNumber: MultipartBody.Part?
    ): SaveInvestmentInfoResponse {
        return apiService.saveInvestmentInfo(
            token,
            customerId,
            transactionTypeId,
            amount,
            paymentTypeId,
            fundId,
            unitTypeId,
            incomeTypeId,
            incomeSpecifyId,
            incomePaymentFrequency,
            paymentReference,
            isAccepted,
            billNumber,
            fundBankAccountNumber,
            IBFTRecepit,
            paymentDate,
            niftStatus,
            IBFTReferenceNumber
        )
    }

    suspend fun getGenerateBillNumber(token: String): GenerateBillNumberResponse {
        return apiService.generateBillNumber(token = token)
    }

    suspend fun getRedemptionInfoLookup(token: String, customerId: String): GetRedemptionInfoResponse {
        return apiService.getRedemptionInfoLookup(token = token, customerId = customerId)
    }

    suspend fun saveRedemptionInfo(token: String, body: RequestBody): SaveRedemptionInfoResponse {
        return apiService.saveRedemptionInfo(token = token, requestBody = body)
    }

    suspend fun getConversionInfoLookup(token: String, customerId: String): GetConversionInfoResponse {
        return apiService.getConversionInfoLookup(token = token, customerId = customerId)
    }

    suspend fun saveConversionInfo(token: String, body: RequestBody): SaveConversionInfoResponse {
        return apiService.saveConversionInfo(token = token, requestBody = body)
    }

    suspend fun getPledgeInfoLookup(token: String, customerId: String): GetPledgeInfoResponse {
        return apiService.getPledgeInfoLookup(token = token, customerId = customerId)
    }

    suspend fun savePledgeInfo(token: String, body: SavePledgeInfoDTO): SavePledgeInfoResponse {
        return apiService.savePledgeInfo(token = token, requestBody = body)
    }
}