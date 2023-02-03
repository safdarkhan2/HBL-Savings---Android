package com.hbl.amc.domain.model

data class SavePledgeInfoResponse(
    val message: String,
    val messageCode: Any,
    val result: SavePledgeInfoResult,
    val status: String,
    val statusTitle: Any
)

data class SavePledgeInfoResult(
    val customerId: String,
    val darNumber: String?,
    val pledgeFormPath: Any,
    val transactionTypeId: String
)