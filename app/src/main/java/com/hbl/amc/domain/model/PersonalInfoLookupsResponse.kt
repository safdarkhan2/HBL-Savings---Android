package com.hbl.amc.domain.model

data class PersonalInfoLookupsResponse(
    val message: String,
    val messageCode: String,
    val result: LookupResult,
    val status: String,
    val statusTitle: String
)

data class LookupResult(
    val countryList: List<Country>,
    val genderList: List<GenericObject>,
    val mobileOwnershipList: List<GenericObject>,
    val religionList: List<GenericObject>,
    val identityTypeList: List<GenericObject>,
    val relationshipsList: List<GenericObject>
)