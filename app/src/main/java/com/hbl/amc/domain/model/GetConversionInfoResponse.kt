package com.hbl.amc.domain.model

data class GetConversionInfoResponse(
    val message: String,
    val messageCode: Any,
    val result: GetConversionInfoResult,
    val status: String,
    val statusTitle: Any
)

data class GetConversionInfoResult(
    val fromFundList: List<FromFund>,
    val fundBalance: Int,
    val incomePlanList: List<ConversionIncomePlan>,
    val instructionsList: List<ConversionInstructions>,
    val paymentFrequencyList: List<ConversionPaymentFrequency>,
    val redumptionRequestList: List<RedumptionRequest>,
    val specifyAmountList: List<ConversionSpecifyAmount>,
    val toFundList: List<ToFund>,
    val unitTypesList: List<ConversionUnitTypes>,
    val disclaimerList: List<ConversionDisclaimer>
)

data class FromFund(
    val id: String,
    val categoryId: String,
    val code: String,
    val description: String,
    val fundBalance: Double,
    val ibftLimitPerDay: Int,
    val isOfferingIncomeUnit: Any,
    val name: String
)

data class ToFund(
    val id: String,
    val categoryId: String,
    val code: String,
    val description: String,
    val fundBalance: Double,
    val ibftLimitPerDay: Int,
    val isOfferingIncomeUnit: Boolean,
    val name: String
)

data class ConversionIncomePlan(
    val id: Int,
    val name: String
)

data class ConversionInstructions(
    val code: String,
    val description: String,
    val title: String
)

data class ConversionPaymentFrequency(
    val id: Int,
    val name: String
)

data class RedumptionRequest(
    val id: Int,
    val name: String
)

data class ConversionSpecifyAmount(
    val id: Int,
    val name: String
)

data class ConversionUnitTypes(
    val id: Int,
    val name: String
)

data class ConversionDisclaimer(
    val id: String,
    val sectionId: String,
    val code: String,
    val title: String,
    val description: String,
    val isRequired: Boolean,
    val isActive: Boolean
)
