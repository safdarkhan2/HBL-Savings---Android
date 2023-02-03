package com.hbl.amc.domain.model

data class SavePersonalInfoResponse(
    var message: String?,
    var messageCode: String,
    var result: SavePersonalInfoResult?,
    var status: String,
    var statusTitle: String
)

/*data class SavePersonalInfoResult(
    var authTransactionTypeID: Int,
    var expiryDate: String,
    var id: String,
    var issueDate: String,
    var isverifyOTP: Boolean,
    var maskMobile: String,
    var statusID: Int,
    var transactionID: String,
    var stepId: String
)*/

data class SavePersonalInfoResult(
    var expiryDate: String,
    var id: String,
    var isExpired: Boolean,
    var isVerified: Boolean,
    var issueDate: String,
    var maskMobile: String,
    var otp: String,
    var stepId: String,
    var token: String,
    var isExistingAccount: Boolean,
    var transactionID: String
)