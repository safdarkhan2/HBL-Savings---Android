package com.hbl.amc.domain.RequestDTO

import com.hbl.amc.domain.model.NextOfKin

data class SaveKYCInfoDTO(
    val id : String,
    val stepId : String,
    val nextOfKinList: List<NextOfKin>,
    val residentStatusId: Int,
    var HasBenificialOwner: Boolean,
//    var identityTypeId: Int,
    var uboAddress: String,
    var uboDOB: String?,
    var uboFatherName: String,
    var uboNIC: String,
    var uboNICExpiryDate: String?,
    var uboNICIssuerDate: String?,
    var uboName: String,
    var uboRelationshipId: Int?
)