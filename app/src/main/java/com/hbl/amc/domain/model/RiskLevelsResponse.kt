package com.hbl.amc.domain.model

data class RiskLevelsResponse(
    var message: String,
    var messageCode: Any?,
    var result: List<RiskLevelsResult>,
    var status: String,
    var statusTitle: Any?
)

data class RiskLevelsResult(
    var createdBy: Any?,
    var createdDate: Any?,
    var id: String,
    var isActive: Boolean,
    var isDeleted: Boolean,
    var maximumScore: Int,
    var minimumScore: Int,
    var riskLevel: String,
    var updatedBy: Any?,
    var updatedDate: Any?
)