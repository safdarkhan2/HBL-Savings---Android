package com.hbl.amc.domain.RequestDTO

data class SaveRpqInfoDTO(
    var age: Int,
    var KeepInvestmentPlanId: Int,
    var EnoughSavingsToSupportId: Int,
    var FinancialGoalsToBeAttainedId: Int,
    var RelateMyselfBestStatementId: Int,
    var FurtherInvestmentIntendId: Int,
    var SubstantialInitialLossId: Int,
    var UsuallyInvestKeepMoneyId: Int,
    var stepId: String,
    var id: String
)