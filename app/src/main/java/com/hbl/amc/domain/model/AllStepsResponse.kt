package com.hbl.amc.domain.model

data class AllStepsResponse(
    var message: String,
    var messageCode: String,
    var result: List<AllStepsResult>,
    var status: String,
    var statusTitle: String
)

data class AllStepsResult(
    var createdBy: Any?,
    var createdDate: Any?,
    var id: String,
    var isActive: Boolean,
    var isDeleted: Boolean,
    var name: String,
    var sequenceNo: Int,
    var shortName: String,
    var stepCode: Int,
    var updatedBy: Any?,
    var updatedDate: Any?
)