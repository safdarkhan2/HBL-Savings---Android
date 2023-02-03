package com.hbl.amc.domain.model

data class SaveDisclaimerResponse(
    val message: String?,
    val messageCode: String?,
    val result: SaveDisclaimerResult?,
    val status: String,
    val statusTitle: String
)

data class SaveDisclaimerResult(
    val customerId: String,
    val stepId : String,
    val disclaimerList: List<SaveDisclaimer>
)

data class SaveDisclaimer(
    val id: String,
    val isAgreed: Boolean
)