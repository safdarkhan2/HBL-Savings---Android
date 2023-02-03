package com.hbl.amc.domain.model

data class GetInvestmentInfoLookupResponse(
    val message: String,
    val messageCode: Any,
    val result: GetInvestmentInfoResult,
    val status: String,
    val statusTitle: Any
)

data class GetInvestmentInfoResult(
    val bankInfo: InvestmentBankInfo,
    val fundsList: List<InvestmentFunds>,
    val incomePlanList: List<InvestmentIncomePlan>,
    val paymentFrequencyList: List<InvestmentPaymentFrequency>,
    val paymentTypesList: List<InvestmentPaymentTypes>,
    val specifyAmountList: List<InvestmentSpecifyAmount>,
    val unitTypesList: List<InvestmentUnitTypes>,
    val instructionsList: List<InvestmentInstructions>,
    val otherFundsList: List<InvestmentFunds>,
    val disclaimerList: List<InvestmentDisclaimer>,
    val bankFundAccountNumber: String
)

data class InvestmentFunds(
    val categoryId: String,
    val id: String,
    val code: String,
    val description: String,
    val name: String,
    val isOfferingIncomeUnit: Boolean,
    val fundBalance: Int,
    var isInstantTransferAllowed: Boolean,
    val ibftLimitPerDay: Int
)

data class InvestmentIncomePlan(
    val id: Int,
    val name: String,
    val description: String
)

data class InvestmentPaymentFrequency(
    val id: Int,
    val name: String
)

data class InvestmentPaymentTypes(
    val id: String,
    val name: String,
    val description: String
)

data class InvestmentSpecifyAmount(
    val id: Int,
    val name: String
)

data class InvestmentUnitTypes(
    val id: Int,
    val name: String
)

data class InvestmentBankInfo(
    val customerNIC: String,
    val availableInvestmentLimit: Int,
    val bankName: String,
    val ibanNumber: String,
    val maximumInvestmentLimit: Int,
    val name: String,
    val totalBalance: Double,
    val transactionAllowed: Int
)

data class InvestmentInstructions(
    val code: String,
    val description: String,
    val title: String
)

data class InvestmentDisclaimer(
    val id: String,
    val sectionId: String,
    val code: String,
    val title: String,
    val description: String,
    val isRequired: Boolean,
    val isActive: Boolean
)
