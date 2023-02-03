package com.hbl.amc.domain.model

data class CountryResponse(
    val message: Any,
    val messageCode: Any,
    val result: CountryResult,
    val status: String,
    val statusTitle: String
)

data class CountryResult(
    val countryList: List<Country>
)

data class Country(
    val id: Int,
    val name: String
)