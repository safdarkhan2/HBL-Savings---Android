package com.hbl.amc.domain.RequestDTO

data class SaveFatcaInfoDTO(
    val W8BenOrW9SubmittedDate: String,
    val cityId: Int,
    val countryId: Int,
    val countryOfBirth: Int,
    val customerName: String,
    val greenCardNo: String,
    val isAccountTransfer: Boolean,
    val isForiegnCitizen: Boolean,
    val isMultiNational: Boolean,
    val isOverseasCareOf: Boolean,
    val isPowerOfAttorney: Boolean,
    val isUSTaxResidence: Boolean,
    val isW8W9Form: Boolean,
    var nat1: Any?,
    var nat2: Any?,
    var nat3: Any?,
    val phoneNo: String,
    val residentialAddess: String,
    val w8W9FormPath: Any?,
    val id: String,
    val stepId: String
)