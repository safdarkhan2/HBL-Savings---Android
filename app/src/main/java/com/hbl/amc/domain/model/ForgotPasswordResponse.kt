package com.hbl.amc.domain.model

data class ForgotPasswordResponse(
    var message: String,
    var messageCode: String?,
    var result: Any?,
    var status: String,
    var statusTitle: String?
)