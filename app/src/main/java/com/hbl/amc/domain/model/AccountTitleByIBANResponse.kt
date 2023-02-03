package com.hbl.amc.domain.model

data class AccountTitleByIBANResponse(
    val message: String,
    val messageCode: String,
    val result: AccountTitleByIBANResult?,
    val status: String,
    val statusTitle: String
)

data class AccountTitleByIBANResult(
    val customerIBAN: String,
    val customerAccountTitle: String,
    val isTitleMatched: Boolean
)