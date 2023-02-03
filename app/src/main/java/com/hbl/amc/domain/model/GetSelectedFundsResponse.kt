package com.hbl.amc.domain.model

import java.io.Serializable

data class GetSelectedFundsResponse(
    val message: String?,
    val messageCode: String?,
    val result: GetSelectedFundsResult?,
    val status: String,
    val statusTitle: String
)

data class GetSelectedFundsResult(
    val id: String,
    val customerId: String,
    val stepId: String,
    val productId: String,
    var fundId: String?,
    var allocationSchemeId: String?,
    var vpsFundCategoryId: String?,
    val equitySubFund: Int,
    val debtSubFund: Int,
    val moneyMarketSubFund: Int,
    val isDisclaimerAgree: Boolean,
    val isOtherFund: Boolean,
    val createdDate: String,
    val createdBy: String,
    val updatedDate: String,
    val updatedBy: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
) : Serializable

