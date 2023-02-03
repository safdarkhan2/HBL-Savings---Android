package com.hbl.amc.domain.model

data class VPSDashboardResponse(
    var message: String,
    var messageCode: String,
    var result: VPSDashboardResult,
    var status: String,
    var statusTitle: String
)

data class VPSDashboardResult(
    var assestWisePortFolioList: List<VPSAssestWisePortFolio>,
    var fundWiseBalance: List<VPSDashboardFundWiseBalance>,
    var lastTenTransactions: List<VPSDashboardLastTenTransaction>,
    var vpsFundsAnalytics: VpsFundsAnalytics
)

data class VPSDashboardFundWiseBalance(
    var amount: String,
    var fundCode: String,
    var fund: String,
    var units: String
)

data class VPSDashboardLastTenTransaction(
    var amount: String,
    var fundName: String,
    var transactionType: String,
    var transactionDate: String,
    var units: String
)

data class VpsFundsAnalytics(
    var frontEndLoadAmount: String?,
    var redemptionAmount: String?,
    var redemptionUnits: String?,
    var investmentAmount: String?,
    var investmentUnits: String?,
    var capitalTax: String?
)

data class VPSAssestWisePortFolio(
    val fundCategory: String,
    val id: String,
    val investmentAmount: Double,
    val investmentPercentage: Double,
    val isMutual: Boolean
)