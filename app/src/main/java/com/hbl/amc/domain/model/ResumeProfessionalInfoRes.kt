package com.hbl.amc.domain.model

data class ResumeProfessionalInfoRes(
    var message: String,
    var messageCode: String,
    var result: ResumeProfessionalResult,
    var status: String,
    var statusTitle: String
)

data class ResumeProfessionalResult(
    var avgAnnaulIncomeId: String?,
    var businessEmployerName: String?,
    var createdBy: Int,
    var createdDate: String,
    var currencyId: Int,
    var customerDesignation: String?,
    var customerOccupationId: String?,
    var customerPublicFigure: Boolean,
    var dividendPayoutId: Int,
    var highValueProfile: Boolean,
    var id: String,
    var isActive: Boolean,
    var isDeleted: Boolean,
    var preciousMetalId: Int?,
    var publicFigureId: Int?,
    var sourceOfIncomeDocument: Any?,
    var sourceOfIncomeId: String,
    var totalWorkingExp: Int,
    var updatedBy: String,
    var updatedDate: String,
    var zakatDeclaration: Boolean,
    var zakatDeclarationDocument: Any?,
    var otherOccupation: String?,
    var othersourceOfIncome: String?,
    var stepId: String,
    var expectedInvestmentId: Int,
    var yearlyExpectedInvestmentId: Int,
    var financialAccountRefusalId: Int,
)