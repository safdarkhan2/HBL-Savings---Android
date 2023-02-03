package com.hbl.amc.domain.model

data class DocumentsTypesResponse(
    var message: String,
    var messageCode: String,
    var result: List<DocumentsTypesResult>,
    var status: String,
    var statusTitle: String
)

data class DocumentsTypesResult(
    var allowedFileExtension: String,
    var description: String,
    var id: Int,
    var maxFileSize: Int,
    var name: String
)