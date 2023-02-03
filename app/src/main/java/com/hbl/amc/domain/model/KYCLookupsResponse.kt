package com.hbl.amc.domain.model

data class KYCLookupsResponse(
    val message: String,
    val messageCode: String,
    val result: KYCResult,
    val status: String,
    val statusTitle: String
)

data class KYCResult(
    var identityTypeList: List<GenericObject>,
    var residentList: List<GenericObject>,
    var uboRelationshipList: List<GenericObject>
)