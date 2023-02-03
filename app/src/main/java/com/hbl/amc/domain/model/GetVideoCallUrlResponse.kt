package com.hbl.amc.domain.model

data class GetVideoCallUrlResponse(
    val message: String,
    val messageCode: Any,
    val result: GetVideoCallUrlResult,
    val status: String,
    val statusTitle: Any
)

data class GetVideoCallUrlResult(
    val redrictURL: String,
    val url: String
)