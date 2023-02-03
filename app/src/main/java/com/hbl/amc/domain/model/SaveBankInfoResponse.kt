package com.hbl.amc.domain.model

data class SaveBankInfoResponse(
    val message: String?,
    val messageCode: String?,
    val result: SaveBankInfoResult,
    val status: String,
    val statusTitle: String
)

data class SaveBankInfoResult(
    val id: String,
    val customerIBAN: String,
    val customerAccountTitle: String,
    val customerBankId: Int,
    val customerBranchId: Int,
    val branchCityId: Int,
    val branchCountryId: Int,
    var stepId: String
)