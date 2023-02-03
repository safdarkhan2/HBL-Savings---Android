package com.hbl.amc.domain.RequestDTO

data class SaveCrsInfoDTO(
    val countryId: Int,
    val reasonId: Int,
    val tinNo: String,
    val id: String,
    val stepId: String,
    val crsCityId: Int
)