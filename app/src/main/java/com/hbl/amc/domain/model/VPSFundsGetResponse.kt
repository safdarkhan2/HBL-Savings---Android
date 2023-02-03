package com.hbl.amc.domain.model

data class VPSFundsGetResponse(
    val message: String?,
    val messageCode: String?,
    val result: List<Fund>?,
    val status: String,
    val statusTitle: String
)

