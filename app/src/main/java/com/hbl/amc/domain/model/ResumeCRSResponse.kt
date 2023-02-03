package com.hbl.amc.domain.model

data class ResumeCRSResponse(
    var message: String,
    var messageCode: String,
    var result: ResumeCRSResult,
    var status: String,
    var statusTitle: String
)

data class ResumeCRSResult(
    var countryId: Int,
    var createdBy: Int,
    var createdDate: String,
    var id: String,
    var isActive: Boolean,
    var isDeleted: Boolean,
    var reasonId: Int,
    var tinNo: String,
    var updatedBy: String,
    var updatedDate: String,
    var crsCityId: Int
)