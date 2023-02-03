package com.hbl.amc.domain.model

data class BankInfoLookupsResponse(
    val message: String,
    val messageCode: String,
    val result: BankInfoLookupResult,
    val status: String,
    val statusTitle: String
)

data class BankInfoLookupResult(
    val banksList: List<Bank>,
    val countryList: List<Country>,
)