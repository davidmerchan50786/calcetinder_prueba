package com.calcetinder_prueba.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.calcetinder_prueba.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /** Email del usuario actual, o cadena vacía si la sesión está rota. */
    val userEmail: String
        get() = authRepository.currentUser?.email ?: ""

    /** UID truncado — suficiente para identificar sin exponer el ID completo en UI. */
    val userIdShort: String
        get() = authRepository.currentUserId?.take(8)?.uppercase() ?: "????????"

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut.asStateFlow()

    fun logout() {
        authRepository.signOut()
        _loggedOut.value = true
    }
}
