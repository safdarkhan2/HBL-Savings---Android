package com.hbl.amc.domain.model

data class CityResponse(
    val message: Any,
    val messageCode: Any,
    val result: CityResult,
    val status: String,
    val statusTitle: String
)

data class CityResult(
    val cities: List<City>
)

data class City(
    val id: Int,
    val name: String
)