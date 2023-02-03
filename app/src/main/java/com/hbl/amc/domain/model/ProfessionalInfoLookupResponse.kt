package com.hbl.amc.domain.model

data class ProfessionalInfoLookupResponse(
    val message: String?,
    val messageCode: String?,
    val result: ProfessionalLookupResult,
    val status: String,
    val statusTitle: String
)

/*data class ProfessionalLookupResult(
    val avgAnnualIncomeList: List<AvgAnnualIncome>,
    val currencyList: List<Currency>,
    val dividendPayoutList: List<DividendPayout>,
    val occupationList: List<Occupation>,
    val preciousMetalList: List<PreciousMetal>,
    val publicFigureList: List<PublicFigure>,
    val sourceOfIncomeList: List<SourceOfIncome>
)

data class AvgAnnualIncome(
    val id: String,
    val name: String
)

data class Currency(
    val currencyCode: String,
    val id: String,
    val name: String
)

data class DividendPayout(
    val id: String,
    val name: String
)

data class Occupation(
    val id: String,
    val name: String
)

data class PreciousMetal(
    val id: String,
    val name: String
)

data class PublicFigure(
    val id: Int,
    val name: String
)

data class SourceOfIncome(
    val id: String,
    val name: String
)*/

//////////////////////////////

data class ProfessionalLookupResult(
    var avgAnnualIncomeList: List<AvgAnnualIncome>,
    var dividendPayoutList: List<DividendPayout>,
    var occupationList: List<Occupation>,
    var preciousMetalList: List<PreciousMetal>,
    var publicFigureList: List<PublicFigure>,
    var sourceOfIncomeList: List<SourceOfIncome>,
    var expectedInvestmentPerTransactionList: List<ExpectedInvestmentPerTransaction>,
    var yearlyExpectedInvestmentList: List<YearlyExpectedInvestment>,
    var financialAccountRefusalList: List<FinancialAccountRefusal>
)

data class AvgAnnualIncome(
    var id: String,
    var name: String
)

data class DividendPayout(
    var id: Int,
    var name: String
)

data class Occupation(
    var id: String,
    var name: String
)

data class PreciousMetal(
    var id: Int,
    var name: String
)

data class FinancialAccountRefusal(
    var id: Int,
    var name: String
)

data class PublicFigure(
    var id: Int,
    var name: String
)

data class ExpectedInvestmentPerTransaction(
    var id: Int,
    var name: String
)

data class YearlyExpectedInvestment(
    var id: Int,
    var name: String
)

data class SourceOfIncome(
    var id: String,
    var name: String
)

