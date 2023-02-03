package com.hbl.amc.domain.RequestDTO

data class SendOtpDTO(
    var OTP: String,
    var expiryDate: String,
    var isExpired: Boolean,
    var isVerified: Boolean,
    var issueDate: String,
    var transactionID: String
)