package com.hbl.amc.domain.RequestDTO

data class VerifyOtpDTO(
    var otp: String,
    var transactionID: String,
    var transactionType: Int
)