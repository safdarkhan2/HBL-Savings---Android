package com.hbl.amc.domain.model

data class VPSFundSaveResponse(
    val message: String?,
    val messageCode: String?,
    val result: VPSFundSaveResult?,
    val status: String,
    val statusTitle: String
)

data class VPSFundSaveResult(
    val customerId: String,
    val stepId: String,
    val fundId: String?,
    val allocationSchemeId: String?,
    val vPSFundCategoryId: String?,
    val equitySubFund: Int?,
    val debtSubFund: Int?,
    val moneyMarketSubFund: Int?,
    val isDisclaimerAgree: Boolean?,
)

