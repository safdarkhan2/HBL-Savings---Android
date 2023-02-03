package com.hbl.amc.domain.model

data class GetRedemptionInfoResponse(
    val message: String,
    val messageCode: Any,
    val result: GetRedemptionInfoResult,
    val status: String,
    val statusTitle: Any
)

data class RedemptionBankInfo(
    val customerNIC: String,
    val availableInvestmentLimit: Int,
    val bankName: String,
    val ibanNumber: String,
    val maximumInvestmentLimit: Int,
    val name: String,
    val totalBalance: Double,
    val transactionAllowed: Int
)

data class RedemptionFunds(
    val id: String,
    val categoryId: String,
    val code: String,
    val description: String,
    val fundBalance: Int,
    val ibftLimitPerDay: Int,
    val isOfferingIncomeUnit: Any,
    val name: String,
    val isInstantTransferAllowed: Boolean
)

data class RedemptionInstructions(
    val code: String,
    val description: String,
    val title: String
)

data class RedemptionRequest(
    val id: Int,
    val name: String
)

data class RedemptionType(
    val description: Any,
    val id: Int,
    val name: String
)

data class GetRedemptionInfoResult(
    val bankInfo: RedemptionBankInfo,
    val fundsList: List<RedemptionFunds>,
    val instructionsList: List<RedemptionInstructions>?,
    val redumptionRequestList: List<RedemptionRequest>,
    val redumptionTypeList: List<RedemptionType>,
    val disclaimerList: List<RedemptionDisclaimer>
)

data class RedemptionDisclaimer(
    val id: String,
    val sectionId: String,
    val code: String,
    val title: String,
    val description: String,
    val isRequired: Boolean,
    val isActive: Boolean
)