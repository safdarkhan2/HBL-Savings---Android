package com.hbl.amc.domain.model

data class SaveInvestmentInfoResponse(
    val message: String,
    val messageCode: Any,
    val result: SaveInvestmentInfoResult,
    val status: String,
    val statusTitle: Any
)

data class SaveInvestmentInfoResult(
    val amount: Double,
    val billNumber: String,
    val createdBy: Any,
    val createdDate: String,
    val customerId: String,
    val fundBankAccountNumber: String,
    val fundId: String,
    val ibftRecepit: Any,
    val id: String,
    val incomePaymentFrequency: Int,
    val incomeSpecifyId: Int,
    val incomeTypeId: Int,
    val isAccepted: Boolean,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val niftStatus: String,
    val paymentDate: String,
    val paymentReference: String,
    val paymentTypeId: String,
    val transactionTypeId: String,
    val unitTypeId: Int,
    val updatedBy: Any,
    val updatedDate: Any,
    var darNumber : String,
    var ibftReferenceNumber : String?
)
