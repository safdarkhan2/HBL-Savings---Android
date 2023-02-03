package com.hbl.amc.domain.model

data class ProductsGetResponse(
    val message: String?,
    val messageCode: String?,
    val result: ProductsGetResult?,
    val status: String,
    val statusTitle: String
)

data class ProductsGetResult(
    val productId: String?,
    val productList: List<Product>?,
)

data class Product(
    val id: String,
    val productCode: String,
    val productName: String,
    val description: String,
    var isSelected: Boolean = false
)

