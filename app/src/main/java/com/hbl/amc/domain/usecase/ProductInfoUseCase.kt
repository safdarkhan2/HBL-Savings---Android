package com.hbl.amc.domain.usecase

import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.repository.ProductInfoRepository
import okhttp3.RequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProductInfoUseCase : KoinComponent {

    private val productInfoRepository by inject<ProductInfoRepository>()

    suspend fun getProducts(token : String, customerId : String): ProductsGetResponse {
        return productInfoRepository.getProducts(token = token, customerId = customerId)
    }

    suspend fun saveProduct(token : String, body: RequestBody): ProductSaveResponse {
        return productInfoRepository.saveProduct(token = token, body = body)
    }

    suspend fun getMutualFunds(token : String, customerId : String): MutualFundsGetResponse {
        return productInfoRepository.getMutualFunds(token = token, customerId = customerId)
    }

    suspend fun getVPSFundsData(token : String, customerId : String): VPSFundsDataGetResponse {
        return productInfoRepository.getVPSFundsData(token = token, customerId = customerId)
    }

    suspend fun getVPSFunds(token : String, categoryId : String): VPSFundsGetResponse {
        return productInfoRepository.getVPSFunds(token = token, categoryId = categoryId)
    }

    suspend fun getSelectedMutualFunds(token : String, customerId : String): GetSelectedFundsResponse {
        return productInfoRepository.getSelectedMutualFunds(token = token, customerId = customerId)
    }

    suspend fun getSelectedVPSFunds(token : String, customerId : String): GetSelectedFundsResponse {
        return productInfoRepository.getSelectedVPSFunds(token = token, customerId = customerId)
    }

    suspend fun saveMutualFund(token : String, body: RequestBody): MutualFundSaveResponse {
        return productInfoRepository.saveMutualFund(token = token, body = body)
    }

    suspend fun saveVPSFund(token : String, body: RequestBody): VPSFundSaveResponse {
        return productInfoRepository.saveVPSFund(token = token, body = body)
    }

    suspend fun saveOtherInfo(token : String, body: RequestBody): SaveOtherInfoResponse {
        return productInfoRepository.saveOtherInfo(token = token, body = body)
    }

    suspend fun getOtherInfo(token : String, customerId : String): SaveOtherInfoResponse {
        return productInfoRepository.getOtherInfo(token = token, customerId = customerId)
    }

    suspend fun getRiskLevels(token : String): RiskLevelsResponse {
        return productInfoRepository.getRiskLevels(token = token)
    }
}