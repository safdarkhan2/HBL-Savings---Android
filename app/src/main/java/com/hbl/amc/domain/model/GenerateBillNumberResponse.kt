package com.hbl.amc.domain.model

data class GenerateBillNumberResponse(
    val message: String,
    val messageCode: Any,
    val result: GenerateBillNumberResult,
    val status: String,
    val statusTitle: Any
)

data class GenerateBillNumberResult(
    val billNumber: Long
)