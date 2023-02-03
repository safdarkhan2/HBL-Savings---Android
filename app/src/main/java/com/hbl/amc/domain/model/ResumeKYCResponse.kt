package com.hbl.amc.domain.model

import java.io.Serializable

data class ResumeKYCResponse(
    var message: String,
    var messageCode: String,
    var result: ResumeKYCResult,
    var status: String,
    var statusTitle: String
)

data class ResumeKYCResult(
    var accountRefusalReason: String?,
    var createdBy: String?,
    var createdDate: String,
    var everRefusedFinAccount: Boolean,
    var hasBenificialOwner: Boolean,
    var id: String,
    var isActive: Boolean,
    var isDeleted: Boolean,
    var nextOfKinList: List<NextOfKin>,
    var residentStatusId: Int,
    var residentStatusTitle: String?,
    var stepId: String,
    var uboAddress: String,
    var uboDOB: String,
    var uboFatherName: String,
    var uboNIC: String,
    var uboNICExpiryDate: String,
    var uboNICIssuerDate: String,
    var uboName: String,
    var uboRelationshipId: Int,
    var updatedBy: String?,
    var updatedDate: String
)
