package com.hbl.amc.domain.model

data class UploadDocumentResponse(
    var message: String,
    var messageCode: String,
    var result: UploadDocumentResult,
    var status: String,
    var statusTitle: String
)

data class UploadDocumentResult(
    var documentName: String,
    var documentTypeId: Int,
    var id: String,
    var isUploaded: Boolean
)