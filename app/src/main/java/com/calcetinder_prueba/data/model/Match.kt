package com.calcetinder_prueba.data.model

import com.google.firebase.Timestamp

data class Match(
    val id: String = "",
    val user1Id: String = "",
    val user2Id: String = "",
    val sock1Id: String = "",   // calcetín del user1 que gustó al user2
    val sock2Id: String = "",   // calcetín del user2 que gustó al user1
    val sock1ImageUrl: String = "",
    val sock2ImageUrl: String = "",
    val sock1Name: String = "",
    val sock2Name: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
