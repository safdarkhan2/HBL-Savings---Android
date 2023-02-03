package com.hbl.amc.domain.RequestDTO

import com.hbl.amc.domain.model.SaveDisclaimer

data class SaveDisclaimerInfoDTO(
    val customerId: String,
    val disclaimerList: List<SaveDisclaimer>,
    val stepId : String
)

