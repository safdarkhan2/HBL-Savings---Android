package com.hbl.amc.domain.model

data class ResumeFatcaResponse(
    var message: String,
    var messageCode: String,
    var result: ResumeFatcaResult,
    var status: String,
    var statusTitle: String
)

data class ResumeFatcaResult(
    var cityId: Int,
    var countryId: Int,
    var countryOfBirth: String,
    var createdBy: Int,
    var createdDate: String,
    var customerName: String,
    var greenCardNo: String?,
    var id: String,
    var isAccountTransfer: Boolean,
    var isActive: Boolean,
    var isDeleted: Boolean,
    var IsUSResident: Boolean,
    var isForiegnCitizen: Boolean,
    var isMultiNational: Boolean,
    var isOverseasCareOf: Boolean,
    var isPowerOfAttorney: Boolean,
    var isUSTaxResidence: Boolean,
    var isW8W9Form: Boolean,
    var nat1: Any?,
    var nat2: Any?,
    var nat3: Any?,
    var phoneNo: String,
    var residentialAddess: String,
    var updatedBy: String,
    var updatedDate: String,
    var w8BenOrW9SubmittedDate: String,
    var w8W9FormPath: String
)