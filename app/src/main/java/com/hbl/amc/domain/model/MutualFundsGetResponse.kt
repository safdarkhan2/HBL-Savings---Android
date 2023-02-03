package com.hbl.amc.domain.model

data class MutualFundsGetResponse(
    val message: String?,
    val messageCode: String?,
    val result: MutualFundsGetResult?,
    val status: String,
    val statusTitle: String
)

data class MutualFundsGetResult(
    val rpqScore: Int,
    val investmentLimit: Int,
    val mutualFundsList: List<Fund>,
    val otherFundsList: List<Fund>?
)

data class Fund(
    val id: String,
    val categoryId: String,
    val code: String,
    val name: String,
    val description: String,
    var isSelected: Boolean = false
)

