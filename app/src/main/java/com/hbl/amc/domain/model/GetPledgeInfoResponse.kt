package com.hbl.amc.domain.model

data class GetPledgeInfoResponse(
    val message: String,
    val messageCode: Any,
    val result: Result,
    val status: String,
    val statusTitle: Any
)

data class Result(
    val instructionsList: List<PledgeInstructions>
)

data class PledgeInstructions(
    val code: String,
    val description: String,
    val title: String
)