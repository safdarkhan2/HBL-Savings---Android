package com.hbl.amc.domain.model

data class SaveRPQInfoResponse(
    val message: String,
    val messageCode: Any,
    val result: SaveRPQInfoResult,
    val status: String,
    val statusTitle: Any
)

data class SaveRPQInfoResult(
    val age: Int,
    val createdBy: Any,
    val createdDate: String,
    val enoughSavingsToSupportId: Int,
    val financialGoalsToBeAttainedId: Int,
    val furtherInvestmentIntendId: Int,
    val id: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val keepInvestmentPlanId: Int,
    val relateMyselfBestStatementId: Int,
    val stepId: String,
    val substantialInitialLossId: Int,
    val updatedBy: Any,
    val updatedDate: Any,
    val usuallyInvestKeepMoneyId: Int
)