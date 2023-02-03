package com.hbl.amc.domain.model

data class SaveOtherInfoResponse(
    val message: String?,
    val messageCode: String?,
    val result: OtherInfoResult?,
    val status: String,
    val statusTitle: String
)

data class OtherInfoResult(
    val age: Int,
    val id: String,
    val ntn: String,
    val retirementAge: Int
)