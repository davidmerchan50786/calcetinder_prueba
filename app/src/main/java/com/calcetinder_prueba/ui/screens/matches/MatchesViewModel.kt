package com.calcetinder_prueba.ui.screens.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calcetinder_prueba.data.model.Match
import com.calcetinder_prueba.data.repository.AuthRepository
import com.calcetinder_prueba.data.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class MatchesUiState(
    val matches: List<Match> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchesUiState())
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

    init { loadMatches() }

    private fun loadMatches() {
        val userId = authRepository.currentUserId ?: run {
            _uiState.value = _uiState.value.copy(isLoading = false)
            return
        }
        matchRepository.getMatches(userId)
            .onEach { matches -> _uiState.value = _uiState.value.copy(matches = matches, isLoading = false) }
            .catch  { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            .launchIn(viewModelScope)
    }
}
