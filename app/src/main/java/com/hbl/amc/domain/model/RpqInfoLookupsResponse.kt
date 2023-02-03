package com.hbl.amc.domain.model

data class RpqInfoLookupsResponse(
    val message: String?,
    val messageCode: String?,
    val result: RpqInfoLookupsResult,
    val status: String,
    val statusTitle: String
)

data class RpqInfoLookupsResult(
    val keepInvestmentPlanList: List<KeepInvestmentPlan>,
    val enoughSavingsToSupportList: List<EnoughSavingsToSupport>,
    val financialGoalsToBeAttainedList: List<FinancialGoalsToBeAttained>,
    val relateMyselfBestStatementList: List<RelateMyselfBestStatement>,
    val furtherInvestmentIntendList: List<FurtherInvestmentIntend>,
    val substantialInitialLossList: List<SubstantialInitialLoss>,
    val usuallyInvestKeepMoneyList: List<UsuallyInvestKeepMoney>
)

data class KeepInvestmentPlan(
    val score: Int,
    val id: Int,
    val name: String
)

data class EnoughSavingsToSupport(
    val score: Int,
    val id: Int,
    val name: String
)

data class FinancialGoalsToBeAttained(
    val score: Int,
    val id: Int,
    val name: String
)

data class RelateMyselfBestStatement(
    val score: Int,
    val id: Int,
    val name: String
)

data class FurtherInvestmentIntend(
    val score: Int,
    val id: Int,
    val name: String
)

data class SubstantialInitialLoss(
    val score: Int,
    val id: Int,
    val name: String
)

data class UsuallyInvestKeepMoney(
    val score: Int,
    val id: Int,
    val name: String
)