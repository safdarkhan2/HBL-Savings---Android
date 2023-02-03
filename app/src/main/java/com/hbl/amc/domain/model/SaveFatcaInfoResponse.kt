package com.hbl.amc.domain.model

data class SaveFatcaInfoResponse(
    val message: String?,
    val messageCode: String?,
    val result: SaveFatcaInfoResult,
    val status: String,
    val statusTitle: String
)

data class SaveFatcaInfoResult(
    var cityId: Int,
    var cityTitle: String,
    var countryId: Int,
    var countryOfBirth: String,
    var countryTitle: String,
    var createdBy: String,
    var createdDate: String,
    var customerName: String,
    var greenCardNo: String,
    var id: String,
    var isAccountTransfer: Boolean,
    var isActive: Boolean,
    var isDeleted: Boolean,
    var isForiegnCitizen: Boolean,
    var isMultiNational: Boolean,
    var isOverseasCareOf: Boolean,
    var isPowerOfAttorney: Boolean,
    var isUSResident: Boolean,
    var isUSTaxResidence: Boolean,
    var isW8W9Form: Boolean,
    var nat1: Any?,
    var nat2: Any?,
    var nat3: Any?,
    var residentialAddess: String,
    var stepId: String,
    var updatedBy: String,
    var updatedDate: String,
    var w8BenOrW9SubmittedDate: String,
    var w8W9FormPath: String
)