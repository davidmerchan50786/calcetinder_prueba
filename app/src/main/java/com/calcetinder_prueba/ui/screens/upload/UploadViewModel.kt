package com.calcetinder_prueba.ui.screens.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calcetinder_prueba.data.repository.AuthRepository
import com.calcetinder_prueba.data.repository.SockRepository
import com.calcetinder_prueba.util.FaceDetector
import com.calcetinder_prueba.util.SatiricCopy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UploadUiState {
    object Idle : UploadUiState()
    object Loading : UploadUiState()
    object Success : UploadUiState()
    data class FaceDetected(val roastMessage: String) : UploadUiState()
    data class Error(val message: String) : UploadUiState()
}

/**
 * FaceDetector se inyecta como interfaz → intercambiable en tests por un fake.
 * En producción, Hilt provee MlKitFaceDetector via DetectionModule.
 */
@HiltViewModel
class UploadViewModel @Inject constructor(
    private val faceDetector: FaceDetector,
    private val sockRepository: SockRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState.Idle)
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    private val _selectedUri = MutableStateFlow<Uri?>(null)
    val selectedUri: StateFlow<Uri?> = _selectedUri.asStateFlow()

    fun onImageSelected(uri: Uri) {
        _selectedUri.value = uri
        _uiState.value = UploadUiState.Idle
    }

    fun submitSock(name: String, description: String) {
        val uri = _selectedUri.value
        val userId = authRepository.currentUserId

        if (uri == null) {
            _uiState.value = UploadUiState.Error("Selecciona una imagen.\nEl calcetín no va a subirse solo.")
            return
        }
        if (name.isBlank()) {
            _uiState.value = UploadUiState.Error("Ponle nombre al calcetín.\nHasta el más ordinario merece uno.")
            return
        }
        if (userId == null) {
            _uiState.value = UploadUiState.Error("Sesión expirada.\nEl tiempo no perdona a nadie.")
            return
        }

        viewModelScope.launch {
            _uiState.value = UploadUiState.Loading

            // Paso 1: detective de egos — si falla la detección, asumimos 0 caras (graceful)
            val faceCount = runCatching { faceDetector.detect(uri) }.getOrDefault(0)
            if (faceCount > 0) {
                _uiState.value = UploadUiState.FaceDetected(SatiricCopy.faceRoast(faceCount))
                return@launch
            }

            // Paso 2: subir el calcetín (que no tiene cara)
            sockRepository.uploadSock(userId, name, description, uri).fold(
                onSuccess = { _uiState.value = UploadUiState.Success },
                onFailure = { _uiState.value = UploadUiState.Error("Error al subir.\n${it.message}") }
            )
        }
    }

    fun resetState() {
        _uiState.value = UploadUiState.Idle
        _selectedUri.value = null
    }
}
