package com.calcetinder_prueba.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// @Singleton: una sola instancia compartida por toda la app durante su ciclo de vida.
// Evita que cada ViewModel cree su propio FirebaseAuth, lo cual sería ineficiente.
@Singleton
class AuthRepository @Inject constructor() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    val currentUser: FirebaseUser? get() = auth.currentUser
    val currentUserId: String? get() = auth.currentUser?.uid
    val isLoggedIn: Boolean get() = auth.currentUser != null

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user!!
            createUserProfile(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun createUserProfile(user: FirebaseUser) {
        db.collection("users").document(user.uid).set(
            hashMapOf(
                "id" to user.uid,
                "email" to (user.email ?: ""),
                "displayName" to (user.displayName ?: ""),
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    fun signOut() = auth.signOut()
}
