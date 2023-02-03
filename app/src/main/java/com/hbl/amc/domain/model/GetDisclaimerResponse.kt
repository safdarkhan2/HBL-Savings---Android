package com.hbl.amc.domain.model

data class GetDisclaimerResponse(
    val message: String,
    val messageCode: Any,
    val result: List<GetDisclaimerResult>,
    val status: String,
    val statusTitle: Any
)

data class GetDisclaimerResult(
    val code: String,
    val description: String,
    val id: String,
    val isRequired: Boolean,
    val sectionId: String,
    val title: String
)