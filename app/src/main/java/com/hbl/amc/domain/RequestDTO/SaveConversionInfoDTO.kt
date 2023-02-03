package com.hbl.amc.domain.RequestDTO

data class SaveConversionInfoDTO(
    val Amount: Double,
    val CustomerId: String,
    val FromFundId: String,
    val IncomePaymentFrequency: Int,
    val IncomeSpecifyId: Int,
    val IncomeTypeId: Int,
    val ToFundId: String,
    val TransactionTypeId: String,
    val UnitTypeId: Int,
    val Units: Any?
)