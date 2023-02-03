package com.hbl.amc.domain.model

data class ResumeRPQResponse(
    val message: String,
    val messageCode: Any,
    val result: ResumeRPQResult,
    val status: String,
    val statusTitle: Any
)

data class ResumeRPQResult(
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
    val updatedDate: String,
    val usuallyInvestKeepMoneyId: Int
)