package com.hbl.amc.domain.RequestDTO

data class SavePersonalInfoDTO(
    var customerAddress: String,
    var customerCityId: Int,
    var customerCountryId: Int,
    var customerDOB: String,
    var customerEmail: String,
    var customerMailingAddress: String,
    var customerMailingCityId: Int,
    var customerMailingCountryId: Int,
    var customerNIC: String,
    var customerName: String,
    var expiryDate: String,
    var genderId: Int,
    var fatherName: String,
    var isLifeTimeExpiry: Boolean,
    var isParmanentAddress: Boolean,
    var issueDate: String,
    var mobileNumber: String,
    var mobileOwnershipId: Int,
    var mothersMaidenName: String,
    var nicBack: String,
    var nicFront: String,
    var religionId: Int,
    var serviceBillProvider: String?,
    var stepId : String,
    var id : String?,
    var accountTypeID: String,
    var education: String,
    var guardianAddress: String,
    var guardianCityId: Int,
    var guardianCountryId: Int,
    var guardianDOB: String?,
    var guardianFatherName: String,
    var guardianNIC: String,
    var guardianNICExpiry: String?,
    var guardianNICIssueDate: String?,
    var guardianName: String,
    var identityTypeId: Int?,
    var minorRelationship: Int,
    var mobileNumberLetterByEmployer: Any?,
    var mobileUndertakingCloseFamily: Any?,
)