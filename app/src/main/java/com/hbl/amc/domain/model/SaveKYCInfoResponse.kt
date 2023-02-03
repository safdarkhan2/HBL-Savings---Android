package com.hbl.amc.domain.model

import java.io.Serializable

data class SaveKYCInfoResponse(
    val message: String,
    val messageCode: String,
    val result: SaveKYCResult,
    val status: String,
    val statusTitle: String
)

data class SaveKYCResult(
    var createdBy: String,
    var createdDate: String,
    var everRefusedFinAccount: Boolean,
    var id: String,
    var isActive: Boolean,
    var isDeleted: Boolean,
    var nextOfKinList: List<NextOfKin>,
    var reason: String,
    var residentStatusId: Int,
    var stepId: String,
    var updatedBy: String,
    var updatedDate: String
)

data class NextOfKin(
    var bForm: String?,
    var customerId: String,
    var emergencyContactNo: String,
    var id: String,
    var identityTypeId: Int,
    var name: String,
    var nic: String,
    var nicBack: String?,
    var nicExpiryDate: String,
    var nicFront: String?,
    var nicIssueDate: String,
    var relationshipId: Int
) : Serializable