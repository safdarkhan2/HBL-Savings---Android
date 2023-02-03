package com.hbl.amc.domain.RequestDTO

data class SaveRedemptionInfoDTO(
    val Amount: Double,
    val CustomerId: String,
    val FundId: String,
    val IBFTLimitPerDay: Any,
    val IsRedemptionAmount: Boolean,
    val RedemptionTypeId: Int,
    val TransactionTypeId: String,
    val Units: Any?
)