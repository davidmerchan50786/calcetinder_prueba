package com.calcetinder_prueba.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calcetinder_prueba.data.repository.AuthRepository
import com.calcetinder_prueba.util.SatiricCopy
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

// @HiltViewModel + @Inject constructor: Hilt crea y gestiona este ViewModel.
// La instancia de AuthRepository es la misma @Singleton en toda la app.
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isAlreadyLoggedIn: Boolean get() = authRepository.isLoggedIn

    fun signIn(email: String, password: String) {
        if (!validate(email, password)) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signIn(email, password).fold(
                onSuccess = { _uiState.value = AuthUiState.Success },
                onFailure = { _uiState.value = AuthUiState.Error(mapFirebaseError(it)) }
            )
        }
    }

    fun signUp(email: String, password: String) {
        if (!validate(email, password)) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signUp(email, password).fold(
                onSuccess = { _uiState.value = AuthUiState.Success },
                onFailure = { _uiState.value = AuthUiState.Error(mapFirebaseError(it)) }
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun validate(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Rellena los campos.\nAunque sea eso.")
            return false
        }
        return true
    }

    private fun mapFirebaseError(e: Throwable): String = when (e) {
        is FirebaseAuthInvalidCredentialsException -> SatiricCopy.AUTH_ERROR_WRONG_PASSWORD
        is FirebaseAuthInvalidUserException        -> SatiricCopy.AUTH_ERROR_NO_USER
        is FirebaseAuthWeakPasswordException       -> SatiricCopy.AUTH_ERROR_WEAK_PASSWORD
        is FirebaseAuthUserCollisionException      -> SatiricCopy.AUTH_ERROR_ALREADY_EXISTS
        else                                       -> SatiricCopy.AUTH_ERROR_GENERIC
    }
}
