package com.calcetinder_prueba.data.repository

import com.calcetinder_prueba.data.model.Match
import com.google.firebase.Firebase
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor() {

    private val db = Firebase.firestore

    /**
     * Devuelve en tiempo real los matches en los que participa [userId].
     * Usa Filter.or() para evitar el índice compuesto que requeriría orderBy en Firestore;
     * el ordenado por fecha se hace en cliente dado el tamaño reducido del dataset.
     */
    fun getMatches(userId: String): Flow<List<Match>> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        try {
            val listener = db.collection("matches")
                .where(
                    Filter.or(
                        Filter.equalTo("user1Id", userId),
                        Filter.equalTo("user2Id", userId)
                    )
                )
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { close(error); return@addSnapshotListener }
                    val matches = snapshot?.documents
                        ?.mapNotNull { doc -> doc.toObject(Match::class.java)?.copy(id = doc.id) }
                        ?.sortedByDescending { it.createdAt }
                        ?: emptyList()
                    trySend(matches)
                }
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            close(e)
        }
    }
}
