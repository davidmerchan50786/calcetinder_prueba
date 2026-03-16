package com.calcetinder_prueba.ui.screens.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calcetinder_prueba.data.model.Sock
import com.calcetinder_prueba.data.repository.AuthRepository
import com.calcetinder_prueba.data.repository.SockRepository
import com.calcetinder_prueba.util.SatiricCopy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SwipeUiState(
    val socks: List<Sock> = emptyList(),
    val isLoading: Boolean = true,
    val matchMessage: String? = null,   // mensaje temporal cuando hay match
    val error: String? = null
)

@HiltViewModel
class SwipeViewModel @Inject constructor(
    private val sockRepository: SockRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SwipeUiState())
    val uiState: StateFlow<SwipeUiState> = _uiState.asStateFlow()

    init { loadSocks() }

    private fun loadSocks() {
        val userId = authRepository.currentUserId ?: return
        sockRepository.getSocksToSwipe(userId)
            .onEach { socks -> _uiState.value = _uiState.value.copy(socks = socks, isLoading = false) }
            .catch  { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            .launchIn(viewModelScope)
    }

    /** Registra un like sobre [sock] y comprueba si hay match mutuo. */
    fun swipeLike(sock: Sock) = swipe(sock, liked = true)

    /** Registra un nope sobre [sock] y lo elimina de la lista. */
    fun swipeNope(sock: Sock) = swipe(sock, liked = false)

    /**
     * Lógica compartida de swipe:
     * 1. Elimina la carta de la UI inmediatamente para UX fluido.
     * 2. Persiste el swipe en Firestore en background.
     * 3. Si hay error de red, reinserta la carta al inicio para que el usuario reintente.
     */
    private fun swipe(sock: Sock, liked: Boolean) {
        val userId = authRepository.currentUserId ?: return
        _uiState.value = _uiState.value.copy(
            socks = _uiState.value.socks.filter { it.id != sock.id }
        )
        viewModelScope.launch {
            sockRepository.swipeOnSock(userId, sock, liked)
                .onSuccess { isMatch ->
                    if (isMatch) {
                        _uiState.value = _uiState.value.copy(
                            matchMessage = "${SatiricCopy.MATCH_FOUND_TITLE}\n\n${SatiricCopy.MATCH_FOUND_BODY}"
                        )
                    }
                }
                .onFailure {
                    // Reinserta la carta al inicio para que el usuario pueda reintentar
                    _uiState.value = _uiState.value.copy(
                        socks = listOf(sock) + _uiState.value.socks,
                        error = it.message
                    )
                }
        }
    }

    /** Oculta el overlay de match una vez que el usuario lo ha visto. */
    fun dismissMatchMessage() {
        _uiState.value = _uiState.value.copy(matchMessage = null)
    }
}
