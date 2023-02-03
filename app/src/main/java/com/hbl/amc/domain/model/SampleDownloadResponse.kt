package com.hbl.amc.domain.model

data class SampleDownloadResponse(
    var message: String,
    var messageCode: String,
    var result: SampleDownloadResult,
    var status: String,
    var statusTitle: String
)

data class SampleDownloadResult(
    var documentBase64: String,
    var extension: String
)