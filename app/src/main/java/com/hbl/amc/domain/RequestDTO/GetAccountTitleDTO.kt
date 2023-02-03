package com.hbl.amc.domain.RequestDTO

data class GetAccountTitleDTO(
    val iban: String,
    val bankId: Int,
    val id: String
)