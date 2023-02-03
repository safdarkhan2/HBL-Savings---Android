package com.hbl.amc.domain.model

data class ResumePersonalInfo(
    var message: String,
    var messageCode: String,
    var result: ResumePersonalResult,
    var status: String,
    var statusTitle: String
)

data class ResumePersonalResult(
    var accountTypeID: String,
    var bForm: String?,
    var createdBy: String?,
    var createdDate: String,
    var customerAddress: String,
    var customerCityId: Int,
    var customerCityTitle: String?,
    var customerCountryId: Int,
    var customerCountryTitle: String?,
    var customerDOB: String,
    var customerEmail: String,
    var customerMailingAddress: String,
    var customerMailingCityId: Int,
    var customerMailingCityTitle: String?,
    var customerMailingCountryId: Int,
    var customerMailingCountryTitle: String?,
    var customerNIC: String?,
    var customerName: String?,
    var education: String,
    var expiryDate: String,
    var fatherName: String?,
    var genderId: Int,
    var genderTitle: String?,
    var guardianAddress: String?,
    var guardianCityId: Int?,
    var guardianCountryId: Int?,
    var guardianDOB: String?,
    var guardianFatherName: String?,
    var guardianNIC: String?,
    var guardianNICExpiry: String?,
    var guardianNICIssueDate: String?,
    var guardianName: String?,
    var id: String,
    var identityTypeId: Int?,
    var isActive: Boolean,
    var isDeleted: Boolean,
    var isLifeTimeExpiry: Boolean,
    var isOwnMobile: Boolean,
    var isParmanentAddress: Boolean,
    var issueDate: String,
    var minorRelationshipId: Int?,
    var mobileNumber: String,
    var mobileNumberLetterByEmployer: Any?,
    var mobileOwnershipId: Int,
    var mobileOwnershipTitle: String?,
    var mobileUndertakingCloseFamily: Any?,
    var mothersMaidenName: String?,
    var nicBack: String?,
    var nicFront: String?,
    var pmdMessage: String,
    var religionId: Int,
    var religionTitle: String?,
    var serviceBillProvider: Any?,
    var stepId: String,
    var updatedBy: String?,
    var updatedDate: String
)