package com.hbl.amc.domain.model

data class SaveVideoCallSlotResponse(
    val message: String,
    val messageCode: Any,
    val result: SaveVideoCallSlotResult,
    val status: String,
    val statusTitle: Any
)

data class SaveVideoCallSlotResult(
    val createdBy: Any,
    val createdDate: String,
    val customerId: String,
    val id: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val slotDateTime: String,
    val updatedBy: Any,
    val updatedDate: Any
)