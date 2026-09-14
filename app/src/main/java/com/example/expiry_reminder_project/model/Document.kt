package com.example.expiry_reminder_project.model

data class Document(
    val id: String,
    var name: String,
    var type: String,
    var documentNumber: String,
    var issueDate: String,
    var expiryDate: String,
    var folderId: String,
    var notes: String
)