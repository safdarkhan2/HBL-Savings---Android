package com.hbl.amc.domain.model

data class SaveConversionInfoResponse(
    val message: String,
    val messageCode: Any,
    val result: SaveConversionInfoResult,
    val status: String,
    val statusTitle: Any
)

data class SaveConversionInfoResult(
    val amount: Double,
    val customerId: String,
    val fromFundId: String,
    val incomePaymentFrequency: Int,
    val incomeSpecifyId: Int,
    val incomeTypeId: Int,
    val isRedemptionAmount: Boolean,
    val toFundId: String,
    val transactionTypeId: String,
    val darNumber: String?,
    val unitTypeId: Int,
    val units: Any
)