package com.calcetinder_prueba.data.model

import com.google.firebase.Timestamp

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
