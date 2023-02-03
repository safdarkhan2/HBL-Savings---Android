package com.hbl.amc.domain.model

data class BranchResponse(
    val message: String?,
    val messageCode: String?,
    val result: BranchResult,
    val status: String,
    val statusTitle: String
)

data class BranchResult(
    val branchlist: List<Branch>
)

data class Branch(
    val bankId: Int,
    val id: Int,
    val branchName: String,
    val branchAddress1: String
)