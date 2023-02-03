package com.hbl.amc.domain.model

data class GetMutualFundDashboardResponse(
    val message: String,
    val messageCode: Any,
    val result: MutualFundDashboardResult,
    val status: String,
    val statusTitle: Any
)

data class MutualFundDashboardResult(
    val assestWisePortFolio: List<AssestWisePortFolio>,
    val capitalGaintax: CapitalGaintax,
    val customerFundwiseBalanceList: List<CustomerFundwiseBalance>,
    val customerTotalBalance: CustomerTotalBalance,
    val dividendData: DividendData,
    val frontEndLoadData: FrontEndLoadData,
    val investmentData: InvestmentData,
    val lastSixTransactionsList: List<LastSixTransactions>,
    val redemptionsData: RedemptionsData,
    val taxOnDividendData: TaxOnDividendData
)

data class AssestWisePortFolio(
    val fundCategory: String,
    val id: String,
    val investmentAmount: Double,
    val investmentPercentage: Double,
    val isMutual: Boolean
)

data class CapitalGaintax(
    val capitalTax: String?,
    val folioNo: String
)

data class CustomerFundwiseBalance(
    val amount: String,
    val fund: String,
    val navValue: String,
    val units: String
)

data class CustomerTotalBalance(
    val totalBalance: String
)

data class DividendData(
    val dividend: String,
    val folioNo: String?,
    val units: String?
)

data class FrontEndLoadData(
    val folioNo: String,
    val frontEndLoad: String?,
    val units: String?
)

data class InvestmentData(
    val folioNo: String,
    val investments: String?,
    val units: String?
)

data class LastSixTransactions(
    val amount: String,
    val cnic: String,
    val folioNo: String,
    val fundName: String,
    val transactionDate: String,
    val transactionSubType: Any,
    val transactionType: String,
    val units: String
)

data class RedemptionsData(
    val folioNo: String,
    val redemptions: String?,
    val units: String?
)

data class TaxOnDividendData(
    val folioNo: String,
    val taxOnDividend: String?
)