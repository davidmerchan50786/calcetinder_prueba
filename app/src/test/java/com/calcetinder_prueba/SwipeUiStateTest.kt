package com.calcetinder_prueba

import com.calcetinder_prueba.data.model.Sock
import com.calcetinder_prueba.ui.screens.swipe.SwipeUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests para SwipeUiState — lógica pura de estado, sin Android ni Firebase.
 *
 * Verifica que las transformaciones de copy() sobre el estado del swipe
 * se comportan exactamente como espera el ViewModel.
 */
class SwipeUiStateTest {

    private fun fakeSock(id: String) = Sock(
        id = id,
        ownerId = "user_1",
        name = "Calcetín $id",
        description = "Un calcetín de prueba",
        imageUrl = "https://example.com/$id.jpg"
    )

    // ── Estado inicial ────────────────────────────────────────────────────────

    @Test
    fun initialState_isLoadingTrue_socksEmpty() {
        val state = SwipeUiState()
        assertTrue(state.isLoading)
        assertTrue(state.socks.isEmpty())
        assertNull(state.matchMessage)
        assertNull(state.error)
    }

    // ── Carga de calcetines ───────────────────────────────────────────────────

    @Test
    fun copyWithSocks_updatesListAndStopsLoading() {
        val socks = listOf(fakeSock("a"), fakeSock("b"), fakeSock("c"))
        val state = SwipeUiState().copy(socks = socks, isLoading = false)

        assertEquals(3, state.socks.size)
        assertTrue(!state.isLoading)
    }

    // ── Eliminación de carta (swipe) ──────────────────────────────────────────

    @Test
    fun removingSock_filtersCorrectly() {
        val socks = listOf(fakeSock("a"), fakeSock("b"), fakeSock("c"))
        val state = SwipeUiState(socks = socks, isLoading = false)

        val updated = state.copy(socks = state.socks.filter { it.id != "b" })

        assertEquals(2, updated.socks.size)
        assertTrue(updated.socks.none { it.id == "b" })
        assertTrue(updated.socks.any { it.id == "a" })
        assertTrue(updated.socks.any { it.id == "c" })
    }

    // ── Match message ─────────────────────────────────────────────────────────

    @Test
    fun setMatchMessage_persistsInState() {
        val state = SwipeUiState(socks = listOf(fakeSock("a")), isLoading = false)
        val withMatch = state.copy(matchMessage = "¡MATCH!")

        assertEquals("¡MATCH!", withMatch.matchMessage)
        // El resto del estado no se toca
        assertEquals(1, withMatch.socks.size)
    }

    @Test
    fun dismissMatchMessage_clearsIt() {
        val state = SwipeUiState(matchMessage = "¡MATCH!", isLoading = false)
        val dismissed = state.copy(matchMessage = null)

        assertNull(dismissed.matchMessage)
    }

    // ── Error state ───────────────────────────────────────────────────────────

    @Test
    fun errorState_setsMessageAndStopsLoading() {
        val state = SwipeUiState()
        val withError = state.copy(isLoading = false, error = "Network error")

        assertEquals("Network error", withError.error)
        assertTrue(!withError.isLoading)
    }
}
