package com.hbl.amc.domain.model

data class BankResponse(
    val message: Any,
    val messageCode: Any,
    val result: BankResult,
    val status: String,
    val statusTitle: String
)

data class BankResult(
    val banksList: List<Bank>
)

data class Bank(
    val id: Int,
    val bankName: String,
    val shortName: String
)