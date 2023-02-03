package com.hbl.amc.domain.model

data class MutualFundSaveResponse(
    val message: String,
    val messageCode: Any,
    val result: MutualFundSaveResult,
    val status: String,
    val statusTitle: String
)

data class MutualFundSaveResult(
    val customerId: String,
    val stepId: String,
    val fundId: String,
    val isDisclaimerAgree: Boolean
)

