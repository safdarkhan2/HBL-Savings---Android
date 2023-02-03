package com.hbl.amc.domain.repository

import com.hbl.amc.api_service.ApiService
import com.hbl.amc.domain.model.*
import okhttp3.RequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProductInfoRepository : KoinComponent {

    private val apiService: ApiService by inject()

    /**
     * Request data from backend
     */
    suspend fun getProducts(token : String, customerId : String): ProductsGetResponse {
        return apiService.getProducts(token = token, customerId = customerId)
    }

    suspend fun saveProduct(token : String, body: RequestBody): ProductSaveResponse {
        return apiService.saveProduct(token = token, requestBody = body)
    }

    suspend fun getMutualFunds(token : String, customerId : String): MutualFundsGetResponse {
        return apiService.getMutualFunds(token = token, customerId = customerId)
    }

    suspend fun getVPSFundsData(token : String, customerId : String): VPSFundsDataGetResponse {
        return apiService.getVPSFundsData(token = token, customerId = customerId)
    }

    suspend fun getVPSFunds(token : String, categoryId : String): VPSFundsGetResponse {
        return apiService.getVPSFunds(token = token, categoryId = categoryId)
    }

    suspend fun getSelectedMutualFunds(token : String, customerId : String): GetSelectedFundsResponse {
        return apiService.getSelectedMutualFunds(token = token, customerId = customerId)
    }

    suspend fun getSelectedVPSFunds(token : String, customerId : String): GetSelectedFundsResponse {
        return apiService.getSelectedVPSFunds(token = token, customerId = customerId)
    }

    suspend fun saveMutualFund(token : String, body: RequestBody): MutualFundSaveResponse {
        return apiService.saveMutualFund(token = token, requestBody = body)
    }

    suspend fun saveVPSFund(token : String, body: RequestBody): VPSFundSaveResponse {
        return apiService.saveVPSFund(token = token, requestBody = body)
    }

    suspend fun saveOtherInfo(token : String, body: RequestBody): SaveOtherInfoResponse {
        return apiService.saveOtherInfo(token = token, requestBody = body)
    }

    suspend fun getOtherInfo(token : String, customerId : String): SaveOtherInfoResponse {
        return apiService.getOtherInfo(token = token, customerId = customerId)
    }

    suspend fun getRiskLevels(token : String): RiskLevelsResponse {
        return apiService.getRiskLevels(token = token)
    }
}