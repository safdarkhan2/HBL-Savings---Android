package com.hbl.amc.domain.RequestDTO

data class SaveProfessionalInfoDTO(
    var id: String,
    var customerOccupationId: String?,
    var customerDesignation: String?,
    var businessEmployerName: String?,
    var totalWorkingExp: Int?,
    var customerPublicFigure: Boolean,
    var publicFigureId: Int,
    var sourceOfIncomeId: String,
    var avgAnnaulIncomeId: String?,
    var highValueProfile: Boolean,
    var preciousMetalId: Int,
    var zakatDeclaration: Boolean,
    var dividendPayoutId: Int,
    var zakatDeclarationDocument: Any?,
    var sourceOfIncomeDocument: Any?,
    var stepId: String,
    var OtherSourceOfIncome: String?,
    var OtherOccupation: String?,
    var ExpectedInvestmentId: Int,
    var YearlyExpectedInvestmentId: Int,
    var FinancialAccountRefusalId: Int

)