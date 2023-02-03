package com.hbl.amc.domain.model

import java.io.Serializable

data class CnicUploadResponse(
    var message: String,
    var messageCode: String,
    var result: CnicUploadResult,
    var status: String,
    var statusTitle: String
)

data class CnicUploadResult(
    var countryId: Int,
    var dateOfBirth: String,
    var expiryDate: String,
    var fatherName: String,
    var fileName: String,
    var genderId: Int,
    var issueDate: String,
    var name: String,
    var nicNo: String,
    var address : String
) : Serializable