package com.hbl.amc.domain.model

data class SaveRedemptionInfoResponse(
    val message: String,
    val messageCode: Any,
    val result: SaveRedemptionInfoResult,
    val status: String,
    val statusTitle: Any
)

data class SaveRedemptionInfoResult(
    val id: String,
    val otp: String,
    val issueDate: String,
    val expiryDate: String,
    val transactionID: String,
    val isVerified: Boolean,
    val isExpired: Boolean,
    val maskMobile: String,
    val darNumber: String
)
