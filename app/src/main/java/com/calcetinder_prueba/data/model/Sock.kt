package com.calcetinder_prueba.data.model

import com.google.firebase.Timestamp

data class Sock(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val uploadedAt: Timestamp = Timestamp.now()
)
