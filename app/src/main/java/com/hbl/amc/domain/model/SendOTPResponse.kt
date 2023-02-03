package com.hbl.amc.domain.model

data class SendOTPResponse(
    val message: String,
    val messageCode: String,
    val result: SendOTPResult,
    val status: String,
    val statusTitle: String
)

data class SendOTPResult(
    val authTransactionTypeID: Int,
    val expiryDate: String,
    val id: String,
    val issueDate: String,
    val isverifyOTP: Boolean,
    val maskMobile: String,
    val statusID: Int,
    val transactionID: String
)

