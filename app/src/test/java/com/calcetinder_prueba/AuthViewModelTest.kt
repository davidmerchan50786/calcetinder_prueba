package com.calcetinder_prueba

import com.calcetinder_prueba.data.repository.AuthRepository
import com.calcetinder_prueba.ui.screens.auth.AuthUiState
import com.calcetinder_prueba.ui.screens.auth.AuthViewModel
import com.calcetinder_prueba.util.MainDispatcherRule
import com.calcetinder_prueba.util.SatiricCopy
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Tests unitarios de AuthViewModel.
 * Se ejecutan en la JVM — sin emulador, sin Firebase real.
 *
 * Cubre:
 *  - Estado inicial
 *  - Validación de campos vacíos
 *  - signIn / signUp éxito → Success
 *  - Mapeo de excepciones Firebase a copy satírico
 *  - resetState → Idle
 *  - isAlreadyLoggedIn delegado al repositorio
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        authRepository = mockk {
            every { isLoggedIn } returns false
        }
        viewModel = AuthViewModel(authRepository)
    }

    // ── Estado inicial ────────────────────────────────────────────────────────

    @Test
    fun initialState_isIdle() {
        assertTrue(viewModel.uiState.value is AuthUiState.Idle)
    }

    @Test
    fun isAlreadyLoggedIn_false_whenNotLoggedIn() {
        every { authRepository.isLoggedIn } returns false
        assertFalse(viewModel.isAlreadyLoggedIn)
    }

    @Test
    fun isAlreadyLoggedIn_true_whenLoggedIn() {
        every { authRepository.isLoggedIn } returns true
        assertTrue(viewModel.isAlreadyLoggedIn)
    }

    // ── Validación ────────────────────────────────────────────────────────────

    @Test
    fun signIn_blankEmail_setsError() = runTest {
        viewModel.signIn("", "password123")
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
    }

    @Test
    fun signIn_blankPassword_setsError() = runTest {
        viewModel.signIn("user@test.com", "")
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
    }

    @Test
    fun signIn_bothBlank_setsError() = runTest {
        viewModel.signIn("", "")
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
    }

    @Test
    fun signUp_blankFields_setsError() = runTest {
        viewModel.signUp("   ", "")
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
    }

    // ── signIn éxito ──────────────────────────────────────────────────────────

    @Test
    fun signIn_success_setsSuccessState() = runTest {
        val fakeUser = mockk<com.google.firebase.auth.FirebaseUser>()
        coEvery { authRepository.signIn("user@test.com", "pass1234") } returns Result.success(fakeUser)

        viewModel.signIn("user@test.com", "pass1234")

        assertEquals(AuthUiState.Success, viewModel.uiState.value)
    }

    // ── signUp éxito ──────────────────────────────────────────────────────────

    @Test
    fun signUp_success_setsSuccessState() = runTest {
        val fakeUser = mockk<com.google.firebase.auth.FirebaseUser>()
        coEvery { authRepository.signUp("new@test.com", "pass1234") } returns Result.success(fakeUser)

        viewModel.signUp("new@test.com", "pass1234")

        assertEquals(AuthUiState.Success, viewModel.uiState.value)
    }

    // ── Mapeo de errores Firebase ─────────────────────────────────────────────

    @Test
    fun signIn_wrongCredentials_mapsToSatiricMessage() = runTest {
        val ex = mockk<FirebaseAuthInvalidCredentialsException>()
        coEvery { authRepository.signIn(any(), any()) } returns Result.failure(ex)

        viewModel.signIn("user@test.com", "wrongpass")

        val state = viewModel.uiState.value as AuthUiState.Error
        assertEquals(SatiricCopy.AUTH_ERROR_WRONG_PASSWORD, state.message)
    }

    @Test
    fun signIn_userNotFound_mapsToSatiricMessage() = runTest {
        val ex = mockk<FirebaseAuthInvalidUserException>()
        coEvery { authRepository.signIn(any(), any()) } returns Result.failure(ex)

        viewModel.signIn("ghost@test.com", "pass1234")

        val state = viewModel.uiState.value as AuthUiState.Error
        assertEquals(SatiricCopy.AUTH_ERROR_NO_USER, state.message)
    }

    @Test
    fun signUp_weakPassword_mapsToSatiricMessage() = runTest {
        val ex = mockk<FirebaseAuthWeakPasswordException>()
        coEvery { authRepository.signUp(any(), any()) } returns Result.failure(ex)

        viewModel.signUp("user@test.com", "123")

        val state = viewModel.uiState.value as AuthUiState.Error
        assertEquals(SatiricCopy.AUTH_ERROR_WEAK_PASSWORD, state.message)
    }

    @Test
    fun signUp_emailAlreadyExists_mapsToSatiricMessage() = runTest {
        val ex = mockk<FirebaseAuthUserCollisionException>()
        coEvery { authRepository.signUp(any(), any()) } returns Result.failure(ex)

        viewModel.signUp("taken@test.com", "pass1234")

        val state = viewModel.uiState.value as AuthUiState.Error
        assertEquals(SatiricCopy.AUTH_ERROR_ALREADY_EXISTS, state.message)
    }

    @Test
    fun signIn_unknownException_mapsToGenericMessage() = runTest {
        coEvery { authRepository.signIn(any(), any()) } returns Result.failure(RuntimeException("unknown"))

        viewModel.signIn("user@test.com", "pass1234")

        val state = viewModel.uiState.value as AuthUiState.Error
        assertEquals(SatiricCopy.AUTH_ERROR_GENERIC, state.message)
    }

    // ── resetState ────────────────────────────────────────────────────────────

    @Test
    fun resetState_fromError_returnsToIdle() = runTest {
        coEvery { authRepository.signIn(any(), any()) } returns Result.failure(RuntimeException())
        viewModel.signIn("user@test.com", "pass1234")
        assertTrue(viewModel.uiState.value is AuthUiState.Error)

        viewModel.resetState()

        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun resetState_fromSuccess_returnsToIdle() = runTest {
        val fakeUser = mockk<com.google.firebase.auth.FirebaseUser>()
        coEvery { authRepository.signIn(any(), any()) } returns Result.success(fakeUser)
        viewModel.signIn("user@test.com", "pass1234")
        assertEquals(AuthUiState.Success, viewModel.uiState.value)

        viewModel.resetState()

        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }
}
