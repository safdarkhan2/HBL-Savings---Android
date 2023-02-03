package com.hbl.amc.domain.RequestDTO

data class SaveBankInfoDTO(
    val customerIBAN: String,
    val customerAccountTitle: String,
    val customerBankId: Int,
    val customerBranchId: Int,
    val branchCityId: Int,
    val branchCountryId: Int,
    val id: String,
    val stepId: String,
    val isRDAAccountType: Boolean,
    val isAccountTitleMatch: Boolean
)