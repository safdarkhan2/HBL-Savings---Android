package com.hbl.amc.domain.RequestDTO

data class ChangeAccountTypeDTO(
    var accountTypeID: String,
    var customerId: String,
    var customerNIC: String,
    var stepId: String
)