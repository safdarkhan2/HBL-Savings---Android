package com.hbl.amc.domain.model

data class CRSInfoLookupsResponse(
    val message: String?,
    val messageCode: String?,
    val result: CrsInfoLookupsResult,
    val status: String,
    val statusTitle: String
)

data class CrsInfoLookupsResult(
    val crsCountries: List<GenericObject>,
    val crsReasons: List<GenericObject>
)