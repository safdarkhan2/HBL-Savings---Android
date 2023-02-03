package com.hbl.amc.domain.model

data class ProductSaveResponse(
    val message: String?,
    val messageCode: String?,
    val result: ProductSaveResult?,
    val status: String,
    val statusTitle: String
)

data class ProductSaveResult(
    val customerId: String,
    val stepId: String,
    val productId: String,
)

