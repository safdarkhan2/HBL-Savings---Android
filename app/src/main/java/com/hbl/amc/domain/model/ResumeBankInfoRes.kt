package com.hbl.amc.domain.model

data class ResumeBankInfoRes(
    var message: String,
    var messageCode: String,
    var result: ResumeBankInfoResult,
    var status: String,
    var statusTitle: String
)

data class ResumeBankInfoResult(
    var branchCityId: Int,
    var branchCountryId: Int,
    var createdBy: Int,
    var createdDate: String,
    var customerAccountTitle: String,
    var customerBankId: Int,
    var customerBranchId: Int,
    var customerIBAN: String,
    var isRDAAccountType: Boolean,
    var id: String,
    var isActive: Boolean,
    var isDeleted: String,
    var updatedBy: String,
    var updatedDate: String
)