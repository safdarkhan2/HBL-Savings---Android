package com.hbl.amc.domain.model

data class VPSFundsDataGetResponse(
    val message: String?,
    val messageCode: String?,
    val result: VPSFundsDataGetResult?,
    val status: String,
    val statusTitle: String
)

data class VPSFundsDataGetResult(
    val rpqScore: Int,
    val investmentLimit: Int,
    val rpqRiskLevel: String,
    val vpsCategoriesList: List<VPSFundCategory>,
    val allocationSchemeList: List<AllocationScheme>
)

data class VPSFundCategory(
    val name: String,
    val code: String,
    val maxAnnualInvLimit: Int,
    val cumulativeInvLimit: Int,
    val maxTransLimit: Int,
    val id: String,
    val createdDate: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    var isSelected : Boolean = false
)

data class AllocationScheme(
    val id: String,
    val productId: String,
    val sequenceNo: Int,
    val code: String,
    val name: String,
    val minEquitySubFund: Int,
    val minDebtSubFund: Int,
    val minMoneyMarketSubFund: Int,
    val equitySubFundTitle: String,
    val debtSubFundTitle: String,
    val moneyMarketSubFundTitle: String,
    val isEditMinEquitySubFund: Boolean,
    val isEditMinDebtSubFund: Boolean,
    val isEditMinMoneyMarketSubFund: Boolean,
    var equitySubFund: Int = 0,
    var debitSubFund: Int = 0,
    var moneyMarketSubFund: Int = 0,
    var isSelected: Boolean,
    var isActive: Boolean,
    var isDeleted: Boolean
)
