package com.hbl.amc.domain.model

data class FatcaInfoLookupsResponse(
    val message: String?,
    val messageCode: String?,
    val result: FatcaLookupsInfoResult,
    val status: String,
    val statusTitle: String
)

data class FatcaLookupsInfoResult(
    val countryList: List<FatcaCountryList>
)

data class FatcaCountryList(
    val id: Int,
    val name: String,
    val countryCode:String,
    val currencyCode:String
)