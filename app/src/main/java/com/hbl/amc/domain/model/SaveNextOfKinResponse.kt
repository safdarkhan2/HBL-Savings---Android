package com.hbl.amc.domain.model

data class SaveNextOfKinResponse(
    var message: String?,
    var messageCode: String,
    var result: SaveNextOfKinResult?,
    var status: String,
    var statusTitle: String
)

data class SaveNextOfKinResult(
    var bForm: String,
    var customerId: String,
    var id: String,
    var name: String,
    var nic: String,
    var nicBack: String,
    var nicExpiryDate: String,
    var nicFront: String,
    var nicIssueDate: String,
    var relationship: String,
    var share: Int
)