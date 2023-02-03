package com.hbl.amc.domain.model

data class AccountTypesResponse(
    var message: String,
    var messageCode: String,
    var result: AccountTypesResult,
    var status: String,
    var statusTitle: String
)

data class AccountTypesResult(
    var accountTypesList: List<AccountTypes>,
    var instructionList: List<Instruction>
)

data class AccountTypes(
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
)

data class Instruction(
    var code: String,
    var description: String,
    var title: String
)