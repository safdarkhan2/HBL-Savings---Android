package com.hbl.amc.domain.model

//data class SaveProfessionalInfoResponse(
//    val message: Any,
//    val messageCode: Any,
//    val result: ProfessionalInfoResult,
//    val status: String,
//    val statusTitle: String
//)
//
//data class ProfessionalInfoResult(
//
//    val id: String,
//    val customerOccupationId: String,
//    val customerDesignation: String,
//    val businessEmployerName: String,
//    val totalWorkingExp: Int,
//    val customerPublicFigure: Boolean,
//    val publicFigureId: Int,
//    val sourceOfIncomeId: String,
//    val avgAnnaulIncomeId: String,
//    val highValueProfile: Boolean,
//    val preciousMetalId: String,
//    val zakatDeclaration: Boolean,
//    val dividendPayoutId: String,
//    val currencyId: String,
//    val zakatDeclarationDocument: String,
//    val sourceOfIncomeDocument: String
//
//)

data class SaveProfessionalInfoResponse (
    val status: String,
    val statusTitle: String,
    val result: ProfessionalInfoResult,
    val message: String? = null,
    val messageCode: String? = null
)

data class ProfessionalInfoResult (
    val id: String,
    val customerOccupationId: String,
    val customerDesignation: String,
    val businessEmployerName: String,
    val totalWorkingExp: Long,
    val customerPublicFigure: Boolean,
    val publicFigureId: Long,
    val sourceOfIncomeId: String,
    val avgAnnaulIncomeId: String,
    val highValueProfile: Boolean,
    val preciousMetalId: String,
    val zakatDeclaration: Boolean,
    val dividendPayoutId: String,
    val currencyId: String,
    val zakatDeclarationDocument: String,
    val sourceOfIncomeDocument: String
)