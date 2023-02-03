package com.hbl.amc.domain.model

data class GetProfileInfoResponse(
    val message: String,
    val messageCode: Any,
    val result: GetProfileInfoResultResult,
    val status: String,
    val statusTitle: Any
)

data class Banks(
    val bankName: String,
    val id: Int,
    val shortName: String
)

data class ProfileCountry(
    val countryCode: String,
    val currencyCode: String,
    val id: Int,
    val name: String
)

data class CustomerDetail(
    val accountTitle: String,
    val bank: String,
    val bankId: Int,
    val branchCityId: Int,
    val branchCountryId: Int,
    val branchId: Int,
    val cnic: String,
    val customerStatus: String,
    val dividendId: Int,
    val email: String,
    val filerStatus: String,
    val folioNumber: String,
    val iban: String,
    val investmentLimit: Int,
    val isZakatExempt: Boolean,
    val mailingAddress: String,
    val mobileNumber: String,
    val name: String,
    val taxApplicable: String
)

data class CustomerProducts(
    val investmentLimit: Int,
    val mutualFundsList: List<MutualFunds>,
    val otherFundsList: List<OtherFunds>,
    val rpqRiskLevel: String,
    val rpqScore: Int
)

data class MutualFunds(
    val categoryId: String,
    val code: String,
    val createdBy: Any,
    val createdDate: Any,
    val description: String,
    val id: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val isInstantTransferAllowed: Boolean,
    val isOfferingIncomeUnit: Boolean,
    val name: String,
    val updatedBy: Any,
    val updatedDate: Any
)

data class CustomerProfileLookUp(
    val banksList: List<Banks>,
    val countryList: List<ProfileCountry>,
    val dividendPayoutList: List<ProfileDividendPayout>
)

data class ProfileDividendPayout(
    val id: Int,
    val name: String
)

data class Documents(
    val documentName: String,
    val documentTypeId: Int,
    val id: String,
    val isEditable: Boolean,
    val isRequired: Boolean,
    val isUploaded: Boolean
)

data class OtherFunds(
    val categoryId: String,
    val code: String,
    val createdBy: Any,
    val createdDate: Any,
    val description: String,
    val id: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val isInstantTransferAllowed: Boolean,
    val isOfferingIncomeUnit: Boolean,
    val name: String,
    val updatedBy: Any,
    val updatedDate: Any
)

data class GetProfileInfoResultResult(
    val customerDetail: CustomerDetail,
    val customerProducts: CustomerProducts,
    val customerProfileLookUp: CustomerProfileLookUp,
    val documentsList: List<Documents>
)