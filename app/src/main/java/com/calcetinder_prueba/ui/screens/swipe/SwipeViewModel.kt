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

    fun swipeLike(sock: Sock) = swipe(sock, liked = true)
    fun swipeNope(sock: Sock) = swipe(sock, liked = false)

    private fun swipe(sock: Sock, liked: Boolean) {
        val userId = authRepository.currentUserId ?: return
        // Eliminar la carta de la lista inmediatamente (UX fluido)
        _uiState.value = _uiState.value.copy(
            socks = _uiState.value.socks.filter { it.id != sock.id }
        )
        viewModelScope.launch {
            sockRepository.swipeOnSock(userId, sock, liked).onSuccess { isMatch ->
                if (isMatch) {
                    _uiState.value = _uiState.value.copy(
                        matchMessage = "${SatiricCopy.MATCH_FOUND_TITLE}\n\n${SatiricCopy.MATCH_FOUND_BODY}"
                    )
                }
            }
        }
    }

    fun dismissMatchMessage() {
        _uiState.value = _uiState.value.copy(matchMessage = null)
    }
}
