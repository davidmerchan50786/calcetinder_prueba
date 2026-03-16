package com.calcetinder_prueba.data.repository

import android.net.Uri
import com.calcetinder_prueba.data.model.Sock
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SockRepository @Inject constructor() {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    /**
     * Devuelve calcetines que el usuario aún no ha visto.
     * Excluye los propios y los ya swiped, filtrando en cliente.
     *
     * NOTA DE ESCALABILIDAD: al crecer el dataset (>10k swipes por usuario),
     * esta carga inicial de IDs vistos debe migrarse a filtrado server-side
     * mediante un índice compuesto en Firestore o una Cloud Function.
     */
    fun getSocksToSwipe(currentUserId: String): Flow<List<Sock>> = callbackFlow {
        val swipedIds = try {
            db.collection("swipes")
                .whereEqualTo("swiperId", currentUserId)
                .get().await()
                .documents
                .mapNotNull { it.getString("sockId") }
                .toSet()
        } catch (e: Exception) {
            emptySet()
        }

        val listener = db.collection("socks")
            .whereNotEqualTo("ownerId", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val socks = snapshot?.documents
                    ?.mapNotNull { doc -> doc.toObject(Sock::class.java)?.copy(id = doc.id) }
                    ?.filter { it.id !in swipedIds }
                    ?: emptyList()
                trySend(socks)
            }
        awaitClose { listener.remove() }
    }

    suspend fun uploadSock(
        userId: String,
        name: String,
        description: String,
        imageUri: Uri
    ): Result<Sock> {
        return try {
            val fileName = "${UUID.randomUUID()}.jpg"
            val imageRef = storage.reference.child("socks/$userId/$fileName")
            imageRef.putFile(imageUri).await()
            val imageUrl = imageRef.downloadUrl.await().toString()

            val sockRef = db.collection("socks").document()
            val sock = Sock(
                id = sockRef.id,
                ownerId = userId,
                name = name.trim(),
                description = description.trim(),
                imageUrl = imageUrl
            )
            sockRef.set(sock).await()
            Result.success(sock)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registra el swipe y detecta si hay match mutuo.
     * @return Result<Boolean> — true si se creó un match.
     */
    suspend fun swipeOnSock(
        currentUserId: String,
        sock: Sock,
        liked: Boolean
    ): Result<Boolean> {
        return try {
            val swipeRef = db.collection("swipes").document()
            swipeRef.set(
                hashMapOf(
                    "id" to swipeRef.id,
                    "swiperId" to currentUserId,
                    "sockId" to sock.id,
                    "sockOwnerId" to sock.ownerId,
                    "direction" to if (liked) "like" else "nope",
                    "timestamp" to FieldValue.serverTimestamp()
                )
            ).await()

            if (!liked) return Result.success(false)

            // Comprueba si el dueño del calcetín ha dado like a algún calcetín nuestro
            val reverseSwipes = db.collection("swipes")
                .whereEqualTo("swiperId", sock.ownerId)
                .whereEqualTo("sockOwnerId", currentUserId)
                .whereEqualTo("direction", "like")
                .limit(1)
                .get().await()

            if (reverseSwipes.isEmpty) return Result.success(false)

            // Match! Obtenemos los datos del calcetín del otro usuario para el match.
            // Si el sockId es nulo o el documento no existe, descartamos silenciosamente
            // el match para evitar escribir datos corruptos en Firestore.
            val theirSock = reverseSwipes.documents.first()
            val theirSockId = theirSock.getString("sockId")
                ?: return Result.success(false)
            val theirSockDoc = db.collection("socks").document(theirSockId).get().await()
            val theirSockData = theirSockDoc.toObject(Sock::class.java)
                ?: return Result.success(false)

            val matchRef = db.collection("matches").document()
            matchRef.set(
                hashMapOf(
                    "id" to matchRef.id,
                    "user1Id" to currentUserId,
                    "user2Id" to sock.ownerId,
                    "sock1Id" to theirSockId,
                    "sock2Id" to sock.id,
                    "sock1ImageUrl" to (theirSockData?.imageUrl ?: ""),
                    "sock2ImageUrl" to sock.imageUrl,
                    "sock1Name" to (theirSockData?.name ?: ""),
                    "sock2Name" to sock.name,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
