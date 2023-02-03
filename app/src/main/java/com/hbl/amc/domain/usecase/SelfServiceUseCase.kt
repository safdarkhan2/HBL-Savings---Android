package com.hbl.amc.domain.usecase

import com.hbl.amc.domain.RequestDTO.SavePledgeInfoDTO
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.repository.SelfServiceRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SelfServiceUseCase : KoinComponent {
    private val selfServiceRepository by inject<SelfServiceRepository>()

    suspend fun getInvestmentInfoLookup(token:String,customerId: String): GetInvestmentInfoLookupResponse {
        return selfServiceRepository.getInvestmentInfoLookups(token = token, customerId = customerId)
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
        return selfServiceRepository.saveInvestmentInfo(
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
        return selfServiceRepository.getGenerateBillNumber(token = token)
    }

    suspend fun getRedemptionInfoLookup(token: String, customerId: String): GetRedemptionInfoResponse {
        return selfServiceRepository.getRedemptionInfoLookup(token = token, customerId = customerId)
    }

    suspend fun saveRedemptionInfo(token: String, body: RequestBody): SaveRedemptionInfoResponse {
        return selfServiceRepository.saveRedemptionInfo(token = token, body = body)
    }

    suspend fun getConversionInfoLookup(token: String, customerId: String): GetConversionInfoResponse {
        return selfServiceRepository.getConversionInfoLookup(token = token, customerId = customerId)
    }

    suspend fun saveConversionInfo(token: String, body: RequestBody): SaveConversionInfoResponse {
        return selfServiceRepository.saveConversionInfo(token = token, body = body)
    }

    suspend fun getPledgeInfoLookup(token: String, customerId: String): GetPledgeInfoResponse {
        return selfServiceRepository.getPledgeInfoLookup(token = token, customerId = customerId)
    }

    suspend fun savePledgeInfo(token: String, body: SavePledgeInfoDTO): SavePledgeInfoResponse {
        return selfServiceRepository.savePledgeInfo(token = token, body = body)
    }
}