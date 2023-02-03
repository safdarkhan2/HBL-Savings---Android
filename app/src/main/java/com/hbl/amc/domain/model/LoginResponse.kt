package com.hbl.amc.domain.model

import java.io.Serializable

data class LoginResponse(
    var message: String?,
    var messageCode: String?,
    var result: LoginResult,
    var status: String,
    var statusTitle: String?
)

/*data class LoginResult(
    var cnic: String,
    var customerId: String,
    var email: String,
    var folioNumber: String?,
    var name: String,
    var token: String
)*/

data class LoginResult(
    var accountType: AccountType,
    var cnic: String,
    var customerIBAN: String,
    var customerId: String,
    var email: String,
    var folioNumber: String,
    var mobileNumber: String,
    var name: String,
    var productId: String,
    var productName: String,
    var token: String
)

data class AccountType(
    var accountLimit: Int,
    var anyTimeInvestment: Int,
    var code: String,
    var createdBy: String?,
    var createdDate: String?,
    var id: String,
    var isActive: Boolean,
    var isDeleted: Boolean?,
    var name: String,
    var products: String,
    var transactionCap: Int,
    var updatedBy: String?,
    var updatedDate: String?
) : Serializable