package com.hbl.amc.domain.model

data class GetPreviewInfoResponse(
    val message: String,
    val messageCode: Any,
    val result: GetPreviewInfoResult,
    val status: String,
    val statusTitle: Any
)

data class GetPreviewInfoResult(
    val personalInfo: PersonalInfo,
    val professionalInfo: ProfessionalInfo,
    val bankInfo: BankInfo,
    val kycInfo: KycInfo,
    val rpqInfo: RpqInfo,
    val fatcaInfo: FatcaInfo,
    val crsInfo: CrsInfo,
    val otherInfo: OtherInfo
)

data class BankInfo(
    val branchCityId: Int,
    val branchCountryId: Int,
    val createdBy: Any,
    val createdDate: String,
    val customerAccountTitle: String,
    val customerBankId: Int,
    val customerBranchId: Int,
    val customerIBAN: String,
    val id: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val stepId: String,
    val updatedBy: Any,
    val updatedDate: String
)

data class CrsInfo(
    val countryId: Int,
    val createdBy: Any,
    val createdDate: String,
    val id: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val reasonId: Int,
    val stepId: String,
    val tinNo: Any,
    val updatedBy: Any,
    val updatedDate: String
)

data class FatcaInfo(
    val cityId: Int,
    val countryId: Int,
    val countryOfBirth: String,
    val createdBy: Any,
    val createdDate: String,
    val customerName: String,
    val greenCardNo: String,
    val id: String,
    val isAccountTransfer: Boolean,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val isForiegnCitizen: Boolean,
    val isMultiNational: Boolean,
    val isOverseasCareOf: Boolean,
    val isPowerOfAttorney: Boolean,
    val isUSTaxResidence: Boolean,
    val isW8W9Form: Boolean,
    val nat1: Any,
    val nat2: Any,
    val nat3: Any,
    val residentialAddess: Any,
    val stepId: String,
    val updatedBy: Any,
    val updatedDate: String,
    val w8BenOrW9SubmittedDate: String,
    val w8W9FormPath: Any
)

data class KycInfo(
    val createdBy: Any,
    val createdDate: String,
    val everRefusedFinAccount: Boolean,
    val id: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val nomineeList: List<Any>,
    val residentStatusId: Int,
    val stepId: String,
    val updatedBy: Any,
    val updatedDate: String
)

data class OtherInfo(
    val createdBy: Any,
    val createdDate: String,
    val id: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val ntn: Any,
    val retirementAge: Any,
    val stepId: String,
    val updatedBy: Any,
    val updatedDate: String
)

data class PersonalInfo(
    val createdBy: Any,
    val createdDate: String,
    val customerAddress: String,
    val customerCityId: Int,
    val customerCountryId: Int,
    val customerDOB: String,
    val customerEmail: String,
    val customerMailingAddress: String,
    val customerMailingCityId: Int,
    val customerMailingCountryId: Int,
    val customerNIC: String,
    val customerName: String,
    val expiryDate: String,
    val genderId: Int,
    val guardianName: String,
    val id: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val isLifeTimeExpiry: Boolean,
    val isParmanentAddress: Boolean,
    val issueDate: String,
    val mobileNumber: String,
    val mobileOwnershipAffidavit: Any,
    val mobileOwnershipId: Int,
    val mothersMaidenName: String,
    val nicBack: Any,
    val nicFront: Any,
    val religionId: Int,
    val serviceBillProvider: Any,
    val stepId: String,
    val updatedBy: Any,
    val updatedDate: String
)

data class ProfessionalInfo(
    val avgAnnaulIncomeId: String,
    val businessEmployerName: String,
    val createdBy: Any,
    val createdDate: String,
    val currencyId: Int,
    val customerDesignation: String,
    val customerOccupationId: String,
    val customerPublicFigure: Boolean,
    val dividendPayoutId: Int,
    val highValueProfile: Boolean,
    val id: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val preciousMetalId: Int,
    val publicFigureId: Int,
    val sourceOfIncomeId: String,
    val stepId: String,
    val updatedBy: Any,
    val updatedDate: String,
    val zakatDeclaration: Boolean
)

data class RpqInfo(
    val age: Int,
    val createdBy: Any,
    val createdDate: String,
    val id: String,
    val investMoneyId: Int,
    val investmentHorizonId: Int,
    val investmentIntendId: Int,
    val investmentObjectiveId: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val knowledgeOfInvestmentId: Int,
    val monthlySavingId: Int,
    val occupationId: String,
    val riskToleranceId: Int,
    val stepId: String,
    val subsInitialLossId: Int,
    val updatedBy: Any,
    val updatedDate: String
)