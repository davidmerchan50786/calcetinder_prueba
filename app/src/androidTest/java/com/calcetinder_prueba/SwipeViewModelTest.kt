package com.calcetinder_prueba

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.calcetinder_prueba.data.model.Sock
import com.calcetinder_prueba.data.repository.AuthRepository
import com.calcetinder_prueba.data.repository.SockRepository
import com.calcetinder_prueba.ui.screens.swipe.SwipeViewModel
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test instrumentado de SwipeViewModel con Hilt.
 *
 * Patrón:
 *  - @HiltAndroidTest activa el grafo de prueba
 *  - @BindValue reemplaza las instancias reales por fakes/mocks
 *  - Verificamos el estado del ViewModel sin tocar Firebase
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SwipeViewModelTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    // Fake de AuthRepository: usuario siempre presente
    @BindValue
    val fakeAuthRepository: AuthRepository = mockk {
        every { currentUserId } returns "test_user_123"
    }

    private val fakeSock = Sock(
        id = "sock_1",
        ownerId = "other_user",
        name = "Calcetín Fantasma",
        description = "Aparece y desaparece sin avisar",
        imageUrl = "https://example.com/sock.jpg"
    )

    // Fake de SockRepository: devuelve un calcetín predefinido
    @BindValue
    val fakeSockRepository: SockRepository = mockk {
        every { getSocksToSwipe("test_user_123") } returns flowOf(listOf(fakeSock))
    }

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun initialLoad_populatesSocks() {
        val viewModel = SwipeViewModel(fakeSockRepository, fakeAuthRepository)
        assertEquals(1, viewModel.uiState.value.socks.size)
        assertEquals("Calcetín Fantasma", viewModel.uiState.value.socks.first().name)
    }

    @Test
    fun swipeLike_removesSockFromList() {
        val viewModel = SwipeViewModel(fakeSockRepository, fakeAuthRepository)
        coEvery {
            fakeSockRepository.swipeOnSock("test_user_123", fakeSock, true)
        } returns Result.success(false)

        viewModel.swipeLike(fakeSock)

        // La carta debe desaparecer de la UI inmediatamente (UX fluido)
        assertEquals(0, viewModel.uiState.value.socks.size)
    }

    @Test
    fun dismissMatchMessage_clearsState() {
        val viewModel = SwipeViewModel(fakeSockRepository, fakeAuthRepository)
        viewModel.dismissMatchMessage()
        assertNull(viewModel.uiState.value.matchMessage)
    }
}
