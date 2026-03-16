package com.calcetinder_prueba.data.model

import com.google.firebase.Timestamp

data class Swipe(
    val id: String = "",
    val swiperId: String = "",
    val sockId: String = "",
    val sockOwnerId: String = "",
    val direction: String = "", // "like" | "nope"
    val timestamp: Timestamp = Timestamp.now()
)
