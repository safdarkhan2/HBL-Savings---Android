package com.hbl.amc.domain.model

data class VerifyOTPResponse(
    var message: String,
    var messageCode: String?,
    var result: VerifyOTPResult,
    var status: String,
    var statusTitle: String?
)

data class VerifyOTPResult(
    var expiryDate: String,
    var id: String,
    var isExpired: Boolean,
    var issueDate: String,
    var isverifyOTP: Boolean,
    var otp: String,
    var transactionID: String
)