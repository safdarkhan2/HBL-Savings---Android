package com.hbl.amc.domain.model

data class SaveCRSInfoResponse(
    val message: String?,
    val messageCode: String?,
    val result: SaveCrsInfoResult,
    val status: String,
    val statusTitle: String
)

data class SaveCrsInfoResult(
    var countryId: Int,
    var countryTitle: String??,
    var createdBy: String?,
    var createdDate: String,
    var id: String,
    var isActive: Boolean,
    var isDeleted: Boolean,
    var reasonId: Int,
    var reasonTitle: String?,
    var stepId: String,
    var tinNo: String,
    var updatedBy: String?,
    var updatedDate: String?
)