package com.hbl.amc.domain.model

data class DocumentChecklistResponse(
    var message: String?,
    var messageCode: String?,
    var result: ArrayList<DocumentChecklistResult>?,
    var status: String,
    var statusTitle: String
)

data class DocumentChecklistResult(
    var documentName: String?,
    var documentTypeId: Int,
    var id: String,
    var isUploaded: Boolean,
    var isRequired: Boolean,
    var isEditable: Boolean
)

data class DocumentChecklistData(
    var documentName: String?,
    var documentTypeId: Int,
    var id: String,
    var isUploaded: Boolean,
    var isRequired: Boolean,
    var isEditable: Boolean,
    var description: String
)