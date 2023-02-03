package com.hbl.amc.domain.RequestDTO

data class SavePledgeInfoDTO(
    val CustomerId: String,
    val PledgeForm: Any?,
    val TransactionTypeId: String
)